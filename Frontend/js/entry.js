const API_BASE = "http://localhost:8080";

// Toggle between login & register forms
document.getElementById("show-register").addEventListener("click", function () {
  document.getElementById("login-form").classList.add("hidden");
  document.getElementById("register-form").classList.remove("hidden");
});

document.getElementById("show-login").addEventListener("click", function () {
  document.getElementById("register-form").classList.add("hidden");
  document.getElementById("login-form").classList.remove("hidden");
});

// Login function
async function doLogin(event) {
    if(event) event.preventDefault();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    if (!email || !password) {
      alert("Enter both email and password");
      return;
    }

    // Try basic auth login
    try{
      const res = await fetch(`${API_BASE}/person/signIn`, {
          method: "POOST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password }),
          credentials: "include"
      });

      if (res.ok) {
        const msg = await res.text();
        alert(msg);

        localStorage.setItem("email", email);
        localStorage.setItem("password", password);

        window.location.href = "index.html"; // Redirect to chat app
      } else {
        alert("Invalid login!");
        console.error("Login failed: " + errorText);
      }
    }catch(err){
      alert("Error connecting to server");
      console.error(err);
    }
}

  // Register function
  async function doRegister(event) {
      if(event) event.preventDefault();

      const name = document.getElementById("regName").value;
      const email = document.getElementById("regEmail").value;
      const password = document.getElementById("regPassword").value;
      const role = document.getElementById("regRole").value;

      if (!name || !email || !password) {
        alert("Fill all fields!");
        return;
      }

      try{
        const res = await fetch(`${API_BASE}/person/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password, role })
        });

        if (res.ok) {
            alert("Registered successfully, now login!");
            document.getElementById("register-form").classList.add("hidden");
            document.getElementById("login-form").classList.remove("hidden");
            
        } else {
            const errorText = await res.text(); // 👈 log backend error
            alert(`Registration failed! Status: ${res.status} ${res.statusText}\n${errorText}`);
            console.error("Registration error:", res.status, res.statusText, errorText);
        }
      }catch(err){
        alert("Error connecting to server");
        console.error(err);
      }
  }

document.getElementById("login-btn").addEventListener("click", doLogin);
document.getElementById("register-btn").addEventListener("click", doRegister);