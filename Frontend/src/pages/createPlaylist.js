import ApiWrapper from '../api/apiWrapper.js';

// Retrieve the userId from local storage
const userId = localStorage.getItem('userId');
const username = localStorage.getItem('username')

document.addEventListener('DOMContentLoaded', function() {
    const backButton = document.getElementById('backToLanding');
    const playlistForm = document.getElementById('create-playlist-form');

    // Redirect to Landing Page
    backButton.addEventListener('click', function() {
        window.location.href = 'landingPage.html';
    });

    // Handle Playlist Creation
    playlistForm.addEventListener('submit', createPlaylist);
});

async function createPlaylist(event) {
    event.preventDefault();

    const playlistName = document.getElementById('playlist-name').value;
    const songName = document.getElementById('song-name').value;
    const songs = [songName];

    try {
        const playlistCreateRequest = {
            playlistName: playlistName,
            songs: songs,
            username: username   // Add the userId here
        };

        const response = await ApiWrapper.addPlaylist(playlistCreateRequest);
        alert('Playlist created successfully!');
        window.location.href = 'myPlaylists.html';
    } catch (error) {
        console.error('Error creating playlist:', error);
        alert('Error creating playlist. Please try again.');
    }
}
