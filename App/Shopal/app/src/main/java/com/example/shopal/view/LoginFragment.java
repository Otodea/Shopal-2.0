package com.example.shopal.view;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.LoginBody;
import com.example.shopal.model.local.POJO.User;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.FragmentUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @BindView(R.id.input_email)
    public EditText emailEditText;

    @BindView(R.id.input_password)
    public EditText passwordEditText;

    @BindView(R.id.btn_login)
    Button loginButton;

    @BindView(R.id.link_signup)
    TextView signUpTextView;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    private String email;
    private String password;

    private User user;

    SharedPreferences sp;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Init
        sp = getActivity().getSharedPreferences("login",MODE_PRIVATE);
        setupLoginButton();
        setupSignUpButton();


    }

    private void setupLoginButton() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                //Checking if fields are empty
                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    emailEditText.setError("Enter a valid email id");
                    return;
                }

                if (TextUtils.isEmpty(password)) {

                    passwordEditText.setError("Can't leave field empty.");
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

                /*Create handle for the RetrofitInstance interface*/
                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

                //Creating Login Body
                LoginBody loginBody = new LoginBody();
                loginBody.setId(email);
                loginBody.setPassword(password);

                Call<User> call = service.login(loginBody);

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

                            Toast.makeText(getActivity(), "Login Failed.", Toast.LENGTH_LONG).show();
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

    private void setupSignUpButton() {

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentUtils.replaceFragment((AppCompatActivity) getActivity(), SignUpFragment.newInstance("test", "test"), R.id.fragment_container, true);


            }
        });

    }



}
