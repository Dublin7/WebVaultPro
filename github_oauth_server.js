// Simple Node.js Express OAuth exchange server
const express = require('express');
const axios = require('axios');
const cors = require('cors');

const CLIENT_ID = 'your_client_id';
const CLIENT_SECRET = 'your_client_secret';

const app = express();
app.use(express.json());
app.use(cors());

app.post('/oauth/callback', async (req, res) => {
    const { code } = req.body;
    try {
        const tokenRes = await axios.post(
            'https://github.com/login/oauth/access_token',
            {
                client_id: CLIENT_ID,
                client_secret: CLIENT_SECRET,
                code: code,
            },
            {
                headers: { Accept: 'application/json' }
            }
        );
        res.json(tokenRes.data);
    } catch (err) {
        res.status(500).json({ error: 'OAuth exchange failed' });
    }
});

app.listen(3000, () => {
    console.log('OAuth server running on http://localhost:3000');
});
