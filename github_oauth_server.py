# Simple Flask OAuth server for GitHub exchange
from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

CLIENT_ID = 'your_client_id'
CLIENT_SECRET = 'your_client_secret'

@app.route('/oauth/callback', methods=['POST'])
def oauth_callback():
    code = request.json.get('code')
    resp = requests.post(
        'https://github.com/login/oauth/access_token',
        headers={'Accept': 'application/json'},
        data={
            'client_id': CLIENT_ID,
            'client_secret': CLIENT_SECRET,
            'code': code
        }
    )
    return jsonify(resp.json())

if __name__ == '__main__':
    app.run(port=3000)
