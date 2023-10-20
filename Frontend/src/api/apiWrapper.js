import axios from 'axios';

class ApiWrapper {

    // User-related API calls
    createUser = async (userCreateRequest) => {
      const response = await axios.post('/user', userCreateRequest);
      return response.data;
    }

   getUserByUsername = async (username) => {
     const response = await axios.get('/user/' + username);
     return response.data;
   }

   login = async (loginRequest) => {
     const response = await axios.post('/user/login', loginRequest);
     return response.data;
   }


  // Playlist-related API calls
  addPlaylist = async (playlistCreateRequest) => {
    const response = await axios.post('/playlists', playlistCreateRequest);
    return response.data;
  }

  getPlaylistById = async (playlistId) => {
    const response = await axios.get('/playlists/' + playlistId);
    return response.data;
  }

  getAllPlaylistsByUserId = async (userId) => {
    const response = await axios.get('/playlists?userId=' + userId);
    return response.data;
  }

 addSongToPlaylist = async (playlistId, songRequest) => {
     const response = await axios.put('/playlists/' + playlistId + '/add-song?songRequest=' + songRequest);
     return response.data;
 }
addUserToPlaylist = async (playlistId, username) => {
    const response = await axios.post('/playlists/' + playlistId + '/users/' + username + '/add');
    return response.data;
}


}

export default new ApiWrapper();
