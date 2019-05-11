package com.example.shopal.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.Contact;
import com.example.shopal.model.local.POJO.LoginBody;
import com.example.shopal.model.local.POJO.Name;
import com.example.shopal.model.local.POJO.User;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SignUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @BindView(R.id.input_first_name)
    public EditText firstNameEditText;

    @BindView(R.id.input_last_name)
    public EditText lastNameEditText;

    @BindView(R.id.input_email)
    public EditText emailEditText;

    @BindView(R.id.input_address)
    public EditText addressEditText;

    @BindView(R.id.input_password)
    public EditText passwordEditText;

    @BindView(R.id.input_mobile)
    public EditText phoneNumberEditText;

    @BindView(R.id.spinner_user_type)
    Spinner userTypeSpinner;

    @BindView(R.id.btn_signup)
    Button signUpButton;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;


    SharedPreferences sp;

    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String mobile;
    private String password;
    private String userType;

    //User Type Spinner Items
    public static ArrayList<String> userTypeSpinnerList = new ArrayList<>(Arrays.asList("User",
            "Driver"));

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Init
        sp = getActivity().getSharedPreferences("login",MODE_PRIVATE);
        setupUserTypeSpinner();
        setupSignUpButton();


    }

    /**
     * Method to setup userType Spinner
     */
    public void setupUserTypeSpinner() {

        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, userTypeSpinnerList);
        userTypeAdapter.setDropDownViewResource(R.layout.spin_item);
        userTypeSpinner.setAdapter(userTypeAdapter);

    }


    private void setupSignUpButton() {

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firstName = firstNameEditText.getText().toString().trim();
                lastName = lastNameEditText.getText().toString().trim();
                address  = addressEditText.getText().toString().trim();
                mobile  = phoneNumberEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                userType = userTypeSpinner.getSelectedItem().toString();

                //Checking if fields are empty
                if (TextUtils.isEmpty(firstName)) {

                    firstNameEditText.setError("Can't leave field empty.");
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {

                    lastNameEditText.setError("Can't leave field empty.");
                    return;
                }
                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    emailEditText.setError("Enter valid email id");
                    return;
                }
                if (TextUtils.isEmpty(address)) {

                    addressEditText.setError("Can't leave field empty.");
                    return;
                }
                if (TextUtils.isEmpty(mobile)) {

                    phoneNumberEditText.setError("Can't leave field empty.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {

                    passwordEditText.setError("Can't leave field empty.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                /*Create handle for the RetrofitInstance interface*/
                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

                //Creating Sign Up Body
                User user = new User();
                user.setId(email);

                Contact contact = new Contact();
                contact.setEmail(email);
                contact.setPhoneNum(mobile);
                contact.setWorkAddr(address);
                user.setContact(contact);

                Name name = new Name();
                name.setFirst(firstName);
                name.setLast(lastName);
                user.setName(name);

                user.setPassword(password);
                user.setUserType(userType);

                Call<User> call = service.signUp(user);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (response.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            User user = response.body();


                            //Saving Login session info
                            sp.edit().putBoolean("logged",true).apply();
                            sp.edit().putString("email",user.getId()).apply();
                            sp.edit().putString("userType",user.getUserType()).apply();
                            sp.edit().putString("name",user.getName().getFirst() + " " + user.getName().getLast()).apply();
                            sp.edit().putString("mobile",user.getContact().getPhoneNum()).apply();
                            sp.edit().putString("address",user.getContact().getWorkAddr()).apply();

                            //Navigate to respective fragments depending on user type
                            if(user.getUserType().equals("User"))
                                FragmentUtils.replaceFragment((AppCompatActivity) getActivity(), UserNavigationFragment.newInstance(user.getId(), user.getUserType(),
                                        user.getName().getFirst() + " " + user.getName().getLast(), user.getContact().getPhoneNum(),user.getContact().getWorkAddr()), R.id.fragment_container, false);
                            else
                                FragmentUtils.replaceFragment((AppCompatActivity) getActivity(), DriverNavigationFragment.newInstance(user.getId(), user.getUserType()), R.id.fragment_container, false);

                        } else {

                            Toast.makeText(getActivity(), "Sign Up Failed.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });

            }
        });

    }


}
