// Toggle between login and signup
function toggleForm(form) {
    document.getElementById("login-form").classList.remove("active");
    document.getElementById("signup-form").classList.remove("active");

    if (form === "login") {
        document.getElementById("form-title").innerText = "Login";
        document.getElementById("login-form").classList.add("active");
    } else {
        document.getElementById("form-title").innerText = "Sign Up";
        document.getElementById("signup-form").classList.add("active");
    }
}

// Handle login
document.getElementById("login-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;
    const email = document.getElementById("login-email").value;
    try {
        const res = await fetch("http://localhost:9000/users/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email , password })
        });
        const data = await res.json();
        //alert("Login response: " + JSON.stringify(data));
        localStorage.setItem("token",data.token);
        window.location.href="dashboard.html";
    } catch (err) {
        alert("Error logging in");
        console.error(err);
    }
});

// Handle signup
document.getElementById("signup-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("signup-username").value;
    const email = document.getElementById("signup-email").value;
    const password = document.getElementById("signup-password").value;

    try {
        const res = await fetch("http://localhost:9000/users/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password })
        });
        const data = await res.json();
        alert("Signup successful! Please login now.");

        toggleForm("login");
    } catch (err) {
        alert("Error signing up");
        console.error(err);
    }
});
