from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS  # Dozvoljava pristup API-ju sa drugih uređaja
import os

app = Flask(__name__)
CORS(app)  # Omogućava CORS za Android aplikaciju

managers = [
    {"id": 1, "ime": "Marko", "prezime": "Mikic", "brojTelefona": "068954221", "email": "mikic@gmail.com", "lozinka": "manager123", "putanja": None},
    {"id": 2, "ime": "Nikola", "prezime": "Nikolic", "brojTelefona": "065953421", "email": "nikolic@gmail.com", "lozinka": "manager345", "putanja": None},
    {"id": 3, "ime": "Nina", "prezime": "Ninic", "brojTelefona": "069984221", "email": "ninic@gmail.com", "lozinka": "manager567", "putanja": None},
    {"id": 4, "ime": "Milos", "prezime": "Misic", "brojTelefona": "063554231", "email": "misic@gmail.com", "lozinka": "manager789", "putanja": None}
]

rental_locations = [
    {"id": 1, "naziv": "Objekat1", "brojTelefona": "064578698", "opis":"nema", "lokacija": "Beograd, Knez Mihailova 1", "putanja": "aldi.jpg", "menadzerId": 1},
    {"id": 2, "naziv": "Objekat2", "brojTelefona": "063568698", "opis":"nema", "lokacija": "Novi Sad, Bulevar Oslobodjenja 10", "putanja": "zest.jpg", "menadzerId": 2},
    {"id": 3, "naziv": "Objekat3", "brojTelefona": "062598698", "opis":"nema", "lokacija": "Subotica, Suboticki put 3", "putanja": "enterprise.jpg", "menadzerId": 3},
    {"id": 4, "naziv": "Objekat4", "brojTelefona": "061571618", "opis":"nema", "lokacija": "Nis, Prizrenska 2", "putanja": "thrifty.jpg", "menadzerId": 4}
]

automobiles = [
    {"id": 1, "proizvodjac": "Tesla", "model": "Model3", "tip": "Sedan", "godiste": 2022, "brojSedista": 5, "kilometraza":3000,  "cenaPoDanu": 145.7, "dostupnost": False, "putanja": "TeslaM3.jpg", "racObjekatId": 1},
    {"id": 2, "proizvodjac": "Toyota", "model": "Corolla", "tip": "Sedan", "godiste": 2020, "brojSedista": 5, "kilometraza": 45000, "cenaPoDanu": 40.5, "dostupnost": True, "putanja": "ToyotaCor.jpg", "racObjekatId": 1},
    {"id": 3, "proizvodjac": "Volkswagen", "model": "Golf 7", "tip": "Hatchback", "godiste": 2019, "brojSedista": 5, "kilometraza": 60000, "cenaPoDanu": 35.0, "dostupnost": True, "putanja": "VolkswagenGolf7.jpg", "racObjekatId": 1},
    {"id": 4, "proizvodjac": "BMW", "model": "X5", "tip": "SUV", "godiste": 2022, "brojSedista": 5, "kilometraza": 25000, "cenaPoDanu": 80.0, "dostupnost": True, "putanja": "BMWx5.jpg", "racObjekatId": 2},
    {"id": 5, "proizvodjac": "Audi", "model": "A4", "tip": "Sedan", "godiste": 2021, "brojSedista": 5, "kilometraza": 30000, "cenaPoDanu": 60.0, "dostupnost": True, "putanja": "AudiA4.jpg", "racObjekatId": 2},
    {"id": 6, "proizvodjac": "Mercedes", "model": "C-Class", "tip": "Sedan", "godiste": 2018, "brojSedista": 5, "kilometraza": 75000, "cenaPoDanu": 55.0, "dostupnost": True, "putanja": "MercedesC.jpg", "racObjekatId": 2},
    {"id": 7, "proizvodjac": "Nissan", "model": "Qashqai", "tip": "SUV", "godiste": 2020, "brojSedista": 5, "kilometraza": 40000, "cenaPoDanu": 50.0, "dostupnost": True, "putanja": "NissanQash.jpg", "racObjekatId": 3},
    {"id": 8, "proizvodjac": "Hyundai", "model": "Tucson", "tip": "SUV", "godiste": 2019, "brojSedista": 5, "kilometraza": 50000, "cenaPoDanu": 45.0, "dostupnost": True, "putanja": "HyundaiTuc.jpg", "racObjekatId": 3},
    {"id": 9, "proizvodjac": "Ford", "model": "Focus", "tip": "Hatchback", "godiste": 2017, "brojSedista": 5, "kilometraza": 90000, "cenaPoDanu": 30.0, "dostupnost": True, "putanja": "FordFocus.jpg", "racObjekatId": 3},
    {"id": 10, "proizvodjac": "Kia", "model": "Sportage", "tip": "SUV", "godiste": 2024, "brojSedista": 5, "kilometraza": 15000, "cenaPoDanu": 55.0, "dostupnost": True, "putanja": "KiaSport.jpg", "racObjekatId": 4},
    {"id": 11, "proizvodjac": "Peugeot", "model": "3008", "tip": "SUV", "godiste": 2020, "brojSedista": 5, "kilometraza": 35000, "cenaPoDanu": 50.0, "dostupnost": True, "putanja": "Peugeot3008.jpg", "racObjekatId": 4}
]

users = [
    {"id": 1, "ime": "Marko", "prezime": "Petrovic", "email": "marko@example.com", "lozinka": "marko123", "brojDozvole": 167890234, "datumIstekaDozvole": "10/10/2025", "datumRodjenja": "2000", "brojTelefona": "065234789", "ulica": "Vojvodjnska 3", "grad": "Novi Sad", "postanskiBroj": "21000", "putanja": None},
    {"id": 2, "ime": "Jovana", "prezime": "Nikolic", "email": "jovana@example.com", "lozinka": "jovana123", "brojDozvole": 257880734, "datumIstekaDozvole": "12/08/2025", "datumRodjenja": "1987", "brojTelefona": "064312675", "ulica": "Svetosavska 10", "grad": "Beograd", "postanskiBroj": "20000", "putanja": None}
]

rentals = [
    {"id": 1, "datumPreuzimanja": "12:30 AM 21/03/2025", "datumVracanja": "12:30 AM 23/03/2025", "brojDana": 2, "autoId": 1, "userId": 1}
]

reviews = [
    {"id": 1, "ocena": 4.5, "komentar": "nemam", "racObjekatId": 1, "userId": 1}
]

# Test ruta (da proverimo da server radi)
@app.route('/')
def home():
    return "Flask server radi!"

@app.route('/api/managers', methods=['GET'])
def get_managers():
    return jsonify(managers)

@app.route('/api/rental_locations', methods=['GET'])
def get_rental_locations():
    return jsonify(rental_locations)

@app.route('/api/search_rental_locations', methods=['GET'])
def search_rental_locations():
    query = request.args.get('query')  # Uzmi upit iz query parametra
    filtered_rental_locations = []

    for automobile in automobiles:
        if (query.lower() in automobile["proizvodjac"].lower() or
            query.lower() in automobile["model"].lower() or
            query.lower() in automobile["tip"].lower() or
            query.lower() in str(automobile["godiste"]) or
            query.lower() in str(automobile["cenaPoDanu"])):
            # Pronađi rent-a-car objekat po ID-u
            rental_location = next((r for r in rental_locations if r["id"] == automobile["racObjekatId"]), None)
            if rental_location and rental_location not in filtered_rental_locations:
                filtered_rental_locations.append(rental_location)

    return jsonify(filtered_rental_locations)

@app.route('/api/automobiles', methods=['GET'])
def get_automobiles():
    return jsonify(automobiles)

@app.route('/api/automobiles_by_id/<int:id>', methods = ['GET'])
def get_automobiles_by_id(id):
    global automobiles
    automobile = next((a for a in automobiles if a["id"] == id), None)
    
    if automobile is None:
        return jsonify({"error": "Automobile not found"}), 404

    return jsonify(automobile)

@app.route('/api/automobiles_izmeni/<int:autoId>', methods=['PUT'])
def update_automobile(autoId):
    data = request.get_json()
    for automobile in automobiles:
        if automobile["id"] == autoId:
            automobile.update(data) 
            return jsonify(automobile), 200
    return jsonify({"error": "Automobil nije pronađen"}), 404

# Endpoint koji vraća JSON podatke
@app.route('/api/users', methods=['GET'])
def get_users():
    return jsonify(users)

# Endpoint za dodavanje novog korisnika (POST)
@app.route('/api/add_user', methods=['POST'])
def add_user():
    data = request.get_json()
    novi_korisnik = {
        "id": len(users) + 1,
        "ime": data["ime"],
        "prezime": data["prezime"],
        "email": data["email"],
        "lozinka": data["lozinka"],
        "brojDozvole": data["brojDozvole"],
        "datumIstekaDozvole": data["datumIstekaDozvole"],
        "datumRodjenja": data["datumRodjenja"],
        "brojTelefona": data["brojTelefona"],
        "ulica": data["ulica"],
        "grad": data["grad"],
        "postanskiBroj": data["postanskiBroj"],
        "putanja": data.get("putanja", None)
    }
    users.append(novi_korisnik)
    return jsonify(novi_korisnik), 201

# Endpoint za brisanje korisnika po ID-u (DELETE)
@app.route('/api/user_delete/<int:id>', methods=['DELETE'])
def delete_user(id):
    global users
    users = [u for u in users if u["id"] != id]
    return jsonify({"message": "Korisnik obrisan"}), 200

# Endpoint za izmenu korisnika
@app.route('/api/users_izmeni/<int:userId>', methods=['PUT'])
def update_user(userId):
    data = request.get_json()
    for user in users:
        if user["id"] == userId:
            user.update(data)  # Ažurira korisnika sa novim podacima
            return jsonify(user), 200
    return jsonify({"error": "Korisnik nije pronađen"}), 404

@app.route('/api/login', methods=['POST'])
def login():
    data = request.json  # Uzimamo JSON podatke iz zahteva
    email = data.get("email")
    lozinka = data.get("lozinka")

    # Proveravamo da li postoji korisnik sa datim email-om i lozinkom
    korisnik = next((user for user in users if user["email"] == email and user["lozinka"] == lozinka), None)

    if korisnik:
        return jsonify({"message": "Prijava uspešna!", "user": korisnik}), 200
    else:
        return jsonify({"message": "Neispravan email ili lozinka!"}), 401

# Endpoint koji vraća JSON podatke
@app.route('/api/rentals', methods=['GET'])
def get_rentals():
    return jsonify({rentals})

@app.route('/api/rentals_search/<int:korisnikId>', methods=['GET'])
def search_rentals(korisnikId):
    global rentals
    rentals = [r for r in rentals if r["userId"] == korisnikId]
    return jsonify(rentals)

@app.route('/api/add_rentals', methods=['POST'])
def add_rental():
    data = request.get_json()
    novi_rental = {
        "id": len(rentals) + 1,
        "datumPreuzimanja": data["datumPreuzimanja"],
        "datumVracanja": data["datumVracanja"],
        "brojDana": data["brojDana"],
        "autoId": data["autoId"],
        "userId": data["userId"]
    }
    rentals.append(novi_rental)
    return jsonify(novi_rental), 201

@app.route('/api/rentals_by_id/<int:id>', methods=['DELETE'])
def delete_rental(id):
    global rentals
    rentals = [r for r in rentals if r["id"] != id]
    return jsonify({"message": "Rental obrisan"}), 200

@app.route('/api/reviews', methods=['POST'])
def add_review():
    data = request.get_json()
    novi_review = {
        "id": len(reviews) + 1,
        "ocena": data["ocena"],
        "komentar": data["komentar"],
        "racObjekatId": data["racObjekatId"],
        "userId": data["userId"]
    }
    reviews.append(novi_review)
    return jsonify(novi_review), 201

@app.route('/static/slike/<path:filename>')
def get_image(filename):
    return send_from_directory(os.path.join(app.root_path, 'static/slike'), filename)

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5000)