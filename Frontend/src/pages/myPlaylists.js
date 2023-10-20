import ApiWrapper from '../api/apiWrapper.js';

document.addEventListener('DOMContentLoaded', async function() {
const backButton = document.getElementById('back-to-landing-button');
    backButton.addEventListener('click', goBackToLandingPage);
    const userId = localStorage.getItem('userId');  // Fetch the logged-in user's ID from local storage
    if (!userId) {
        console.error('User not logged in');
        return;  // If there's no user ID, then the user isn't logged in
    }

    try {
        const playlists = await ApiWrapper.getAllPlaylistsByUserId(userId);

        const playlistList = document.getElementById('playlist-list');
        playlistList.innerHTML = ''; // Clear any previous content

        playlists.forEach(playlist => {
            const playlistItem = document.createElement('div');
            playlistItem.className = 'playlist-item';
            playlistItem.textContent = playlist.playlistName;
            playlistItem.addEventListener('click', () => viewPlaylist(playlist.playlistId));  // This is where you get the playlist ID.
            playlistList.appendChild(playlistItem);
        });
    } catch (error) {
        console.error('Error fetching playlists:', error);
        alert('Error fetching your playlists. Please try again.');
    }
});

function viewPlaylist(id) {
    // Redirect to the playlist view page
    window.location.href = `myPlaylistView.html?id=${id}`;
}

function goBackToLandingPage() {
    window.location.href = 'landingPage.html';
}