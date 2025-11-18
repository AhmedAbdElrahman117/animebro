package com.example.animbro


object Constants {
    val emailRegex = Regex(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@" +                   // local part
                "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?" +        // domain label start
                "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$" // optional dot-separated labels
    );
    val lengthRegex = Regex("""^.{6,}$""")
    val uppercaseRegex = Regex(""".*[A-Z].*""")
    val specialCharRegex = Regex(""".*[!@#$%^&*(),.?":{}|<>].*""")
    val twoDigitsRegex = Regex("^(.*\\d){2,}.*$")
}