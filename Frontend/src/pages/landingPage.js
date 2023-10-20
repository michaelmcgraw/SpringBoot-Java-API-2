import ApiWrapper from '../api/apiWrapper.js';

document.addEventListener('DOMContentLoaded', async function() {
    const logoutButton = document.getElementById('logout-button');
    logoutButton.addEventListener('click', logout);

    const userId = localStorage.getItem('userId');
    if (!userId) {
        console.error('User not logged in');
        return;
    }

    // Retrieve and display username
    const username = localStorage.getItem('username');
    const usernameDisplay = document.getElementById('username-display');
    usernameDisplay.textContent = username;

    try {
            const myPlaylistsData = await ApiWrapper.getAllPlaylistsByUserId(userId);
            const myPlaylistsDiv = document.getElementById('my-playlists');

            myPlaylistsData.forEach(playlist => {
                const playlistItem = document.createElement('li');
                playlistItem.className = 'playlist-item';
                playlistItem.textContent = playlist.playlistName;

                // Create a container for the songs within this playlist
                const songList = document.createElement('ul');
                songList.className = 'song-list';

                // Populate song list with songs
                playlist.songs.forEach(songName => {
                    const songListItem = document.createElement('li');
                    songListItem.className = 'song-list-item';
                    songListItem.textContent = songName;
                    songList.appendChild(songListItem);
                });

                playlistItem.appendChild(songList);
                myPlaylistsDiv.appendChild(playlistItem);

                // Add click event listener to toggle visibility
                playlistItem.addEventListener('click', function() {
                    songList.classList.toggle('show-songs');
                });
            });
        } catch (error) {
            console.error('Error fetching playlists:', error);
        }
    });

            function logout() {
                localStorage.removeItem('userId'); // Clear the user ID from local storage
                localStorage.removeItem('username'); // Clear the username from local storage
                window.location.href = 'login.html';  // Redirect to the login page or another landing page
            }
