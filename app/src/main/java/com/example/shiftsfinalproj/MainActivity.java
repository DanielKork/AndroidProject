package com.example.shiftsfinalproj;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize the ShiftsFragment
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new ShiftsFragment())
//                    .commit();
//        }
//    }
//}


//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new LoginFragment())
//                    .commit();
//        }
//    }
//}

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SignupFragment())
                    .commit();
        }
    }
}





//package com.example.shiftsfinalproj;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // If using BottomNavigationView, set it up here
//        // setupBottomNavigationView();
//    }
//
//    // Optional: Method to set up BottomNavigationView
////    private void setupBottomNavigationView() {
////        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
////        bottomNav.setOnNavigationItemSelectedListener(item -> {
////            Fragment selectedFragment = null;
////
////            switch (item.getItemId()) {
////                case R.id.nav_shifts:
////                    selectedFragment = new ShiftsFragment();
////                    break;
////                case R.id.nav_profile:
////                    selectedFragment = new ProfileFragment();
////                    break;
////                // Add cases for other menu items
////            }
////
////            getSupportFragmentManager().beginTransaction()
////                    .replace(R.id.fragment_container, selectedFragment)
////                    .commit();
////
////            return true;
////        });
////    }
//}
