/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener , View.OnKeyListener {

  // Declarations
  EditText usernameInput;
  EditText passwordInput;
  Boolean signUpModeIsActive = true;
  Button signUp;
  TextView logIn;
  ConstraintLayout constraintLayout;
  ImageView logo;


  // go to usersActivity and show the list of users
  public void showUsersList() {
    Intent intent = new Intent(MainActivity.this, UsersActivity.class);
    startActivity(intent);

    ParseUser.getQuery().findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> objects, ParseException e) {
        if (objects.size() > 0) {
          for (int i = 0; i < objects.size(); i++) {
            UsersActivity.users.add(objects.get(i).getUsername());
            UsersActivity.arrayAdapter.notifyDataSetChanged();
          }

        } else {
          Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

      }
    });
  }


  // Do an action when user click the enter key
  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
      signUp(v);
    }

    return false;
  }


  //switch between sign up & login Buttons
  //
  // Hide the keyboard when user click over background or instagram logo
  @Override
  public void onClick(View v) {

    if (v.getId() == R.id.loginButton) {

      if (signUpModeIsActive) {
        signUpModeIsActive = false;
        signUp.setText("Login");
        logIn.setText("Or, Sign up");

      } else {
        signUpModeIsActive = true;
        signUp.setText("Sign up");
        logIn.setText("Or, Login");

      }
    } else if (v.getId() == R.id.imageView || v.getId() == R.id.ConstraintLayout) {

      InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }
  }


  // sign up and log in keys function
  public void signUp(View view) {

    if (signUpModeIsActive) {


      if (usernameInput.getText().toString().equals("") || passwordInput.getText().toString().equals("")) {
        Toast.makeText(this, "A username & password are require", Toast.LENGTH_SHORT).show();
      } else {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(usernameInput.getText().toString());
        parseUser.setPassword(passwordInput.getText().toString());

        parseUser.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
              showUsersList();

            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    } else {

      // log in mode

      ParseUser.logInInBackground(usernameInput.getText().toString(), passwordInput.getText().toString(), new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
          if (user != null) {
            Toast.makeText(MainActivity.this, "You successfully Login ;)", Toast.LENGTH_SHORT).show();

            showUsersList();

          } else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }

        }
      });


    }
  }


  // on Create Method
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Instagram");

    logo = findViewById(R.id.imageView);
    constraintLayout =  findViewById(R.id.ConstraintLayout);
    usernameInput = findViewById(R.id.usernameInput);
    passwordInput = findViewById(R.id.passwordInput);
    logIn = findViewById(R.id.loginButton);
    signUp = findViewById(R.id.signUpButton);
    passwordInput.setOnKeyListener(this);
    logo.setOnClickListener(this);
    constraintLayout.setOnClickListener(this);
    logIn.setOnClickListener(this);

    if (ParseUser.getCurrentUser() != null) {

      showUsersList();

    }



    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }
}