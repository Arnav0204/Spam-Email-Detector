async function predictEmail() {
  const emailText = document.getElementById("emailText").value;
  const resultDiv = document.getElementById("result");

  if (!emailText.trim()) {
    resultDiv.innerText = "⚠️ Please paste an email first.";
    return;
  }

  try {
    const res = await fetch("http://localhost:9000/email/predict", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token") // if using JWT
      },
      body: JSON.stringify({ text : emailText })
    });

    const data = await res.json();
    console.log(data);

    // assume API returns { prediction: "spam" } or { prediction: "ham" }
    resultDiv.innerText = `Prediction: ${data.prediction.toUpperCase()}`;
  } catch (err) {
    resultDiv.innerText = "❌ Error predicting spam.";
    console.error(err);
  }
}
