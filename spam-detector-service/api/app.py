from flask import Flask, request, jsonify
import joblib, os, re
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
from dotenv import load_dotenv


# Get BASE_DIR as spam-detector-service directory
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
print("BASE_DIR : ",BASE_DIR)
ENV_PATH = os.path.join(BASE_DIR, ".env")
print ("ENV_PATH : ",ENV_PATH)
load_dotenv(dotenv_path=ENV_PATH)


# Paths from env or defaults
MODEL_PATH = os.path.join(BASE_DIR, os.getenv("MODEL_PATH"))
VECTORIZER_PATH = os.path.join(BASE_DIR, os.getenv("VECTORIZER_PATH"))

# Load model and vectorizer once
print(f"Loading model from: {MODEL_PATH}")
print(f"Loading vectorizer from: {VECTORIZER_PATH}")
nb_model = joblib.load(MODEL_PATH)
vectorizer = joblib.load(VECTORIZER_PATH)
print("Model and vectorizer loaded successfully!")

# Initialize NLP tools once
stop_words = set(stopwords.words('english'))
stemmer = PorterStemmer()

def preprocess_text(text):
    text = text.lower()
    text = re.sub(r'[^\w\s]', '', text)
    words = text.split()
    words = [stemmer.stem(word) for word in words if word not in stop_words]
    return ' '.join(words)

def predict_spam(text):
    cleaned = preprocess_text(text)
    vec = vectorizer.transform([cleaned])
    label = nb_model.predict(vec)[0]
    prob = nb_model.predict_proba(vec)[0][1]
    return label, prob

# Flask app
app = Flask(__name__)

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    if not data or "text" not in data:
        return jsonify({"error": "Missing 'text' field"}), 400

    label, prob = predict_spam(data["text"])
    return jsonify({
        "prediction": "Spam" if label == 1 else "Ham",
        "label": int(label),
        "probability": round(float(prob), 4)
    })

if __name__ == "__main__":
    import nltk
    nltk.download('stopwords')
    app.run(host="0.0.0.0", port=8000, debug=True)
