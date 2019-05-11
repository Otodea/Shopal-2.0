package com.example.shopal.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.LoginBody;
import com.example.shopal.model.local.POJO.User;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.FragmentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserFragment extends Fragment {

    private static final String EMAIL = "param1";
    private static final String USER_TYPE = "param2";


    @BindView(R.id.user_type_image_view)
    public ImageView userTypeImageView;

    @BindView(R.id.email)
    public TextView emailTextView;
    @BindView(R.id.name)
    public TextView nameTextView;
    @BindView(R.id.address)
    public TextView addressTextView;
    @BindView(R.id.mobile)
    public TextView mobileTextView;


    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    private String email;
    private String userType;

    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance(String email, String userType) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            userType = getArguments().getString(USER_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_user, container, false);
         ButterKnife.bind(this, view);
         return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateUI();



    }


    private void populateUI(){

        //Setting image View depending on userType
        if(userType.equals("User"))
            userTypeImageView.setImageResource(R.drawable.man);
        else
            userTypeImageView.setImageResource(R.drawable.delivery_man);
        emailTextView.setText(email);

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<User> call = service.getUser(email);
        progressBar.setVisibility(View.GONE);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);

                    User user = response.body();

                    nameTextView.setText(user.getName().getFirst() + " " + user.getName().getLast() );
                    addressTextView.setText(user.getContact().getWorkAddr());
                    mobileTextView.setText(user.getContact().getPhoneNum());

                } else {

                    Toast.makeText(getActivity(), "Failed to get user info", Toast.LENGTH_LONG).show();
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


}
