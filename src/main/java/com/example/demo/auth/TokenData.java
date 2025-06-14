package com.example.demo.auth;

public class TokenData {

    private static ThreadLocal<String> userId = new ThreadLocal<>();
    private static ThreadLocal<String> firstName = new ThreadLocal<>();
    private static ThreadLocal<String> lastName = new ThreadLocal<>();
    private static ThreadLocal<String> email = new ThreadLocal<>();
    private static ThreadLocal<String> phoneNumber = new ThreadLocal<>();
    private static ThreadLocal<String> userRole = new ThreadLocal<>();


    public static String getUserId (){ return userId.get(); }
    public static void setUserId (String id){ userId.set(id); }

    public static String getFirstName (){ return firstName.get(); }
    public static void setFirstName (String fname){ firstName.set(fname); }

    public static String getLastName (){ return lastName.get(); }
    public static void setLastName (String lName){ lastName.set(lName); }

    public static String getEmail (){ return email.get(); }
    public static void setEmail (String mail){ email.set(mail); }

    public static String getPhoneNumber (){ return phoneNumber.get(); }
    public static void setPhoneNumber (String number){ phoneNumber.set(number); }

    public static String getUserRole (){ return userRole.get(); }
    public static void setUserRole (String role){ userRole.set(role); }


    public static void cleanup() {
        userId.remove();
        firstName.remove();
        lastName.remove();
        email.remove();
        phoneNumber.remove();
        userRole.remove();
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(TokenData::cleanup));
    }
}
