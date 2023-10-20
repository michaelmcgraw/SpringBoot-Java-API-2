import ApiWrapper from '../api/apiWrapper.js';

const playlistId = new URL(window.location.href).searchParams.get('id'); // Get playlist ID from URL

document.addEventListener('DOMContentLoaded', async function() {
    const addSongButton = document.getElementById('add-song-btn');
    addSongButton.addEventListener('click', addSong);

    const addCollaboratorButton = document.getElementById('add-collaborator-btn');
    addCollaboratorButton.addEventListener('click', addCollaborator);


    try {
        const playlist = await ApiWrapper.getPlaylistById(playlistId);
        renderSongs(playlist.songs);

    } catch (err) {
        console.error('Error fetching playlist details:', err);
        alert('Error fetching playlist details.');
    }

    const backButton = document.getElementById('back-button');
    backButton.addEventListener('click', goBackToMyPlaylists);
});

function goBackToMyPlaylists() {
    window.location.href = 'myPlaylists.html';
}

async function addSong() {
    const songRequest = document.getElementById('new-song-name').value;

    try {
        await ApiWrapper.addSongToPlaylist(playlistId, songRequest);
        alert('Song added successfully!');
        location.reload();  // Refresh the page to display the updated playlist
    } catch (err) {
        console.error('Error adding song:', err);
        alert('Error adding song. Please try again.');
    }
}

async function addCollaborator() {
    const username = document.getElementById('new-collaborator-username').value;
    try {
        await ApiWrapper.addUserToPlaylist(playlistId, username);
        alert('Collaborator added successfully!');
        location.reload();  // Refresh the page to display the updated collaborators
    } catch (err) {
       location.reload();
    }
}

function renderSongs(songs) {
    const songsList = document.querySelector('.songs-list');
    songsList.innerHTML = ''; // Clear existing content

    songs.forEach(song => {
        const songElement = document.createElement('div');
        songElement.className = 'song-item';
        songElement.textContent = song; // Song is a string in this case
        songsList.appendChild(songElement);
    });
}



