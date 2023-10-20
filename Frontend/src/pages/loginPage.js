import ApiWrapper from '../api/apiWrapper.js';

document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.getElementById('login-form');
  const createUserForm = document.getElementById('create-user-form');

  // Login code
  loginForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
      const loginRequest = {
        username: username,
        password: password
      }

      const data = await ApiWrapper.login(loginRequest);
      console.log('Login Response:', data);
      if (data) {
        alert('Logged in as ' + username);
        localStorage.setItem('username', username)
        localStorage.setItem('userId', data);
        localStorage.setItem('sessionId', 'mock-session-id');
        window.location.href = 'landingPage.html';
      } else {
        alert(`Login failed`);
      }
    } catch (error) {
      alert('An error occurred during login.');
      console.error('Error during login:', error);
    }
  });

  // User creation code
  createUserForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    const userCreateRequest = {
      username: document.getElementById('create-username').value,
      password: document.getElementById('create-password').value,
      email: document.getElementById('create-email').value,
      playlists: []
    };

    try {
      const data = await ApiWrapper.createUser(userCreateRequest);
      console.log('Create User Response:', data);
      alert(`User created: ${userCreateRequest.username}`);
      localStorage.setItem('userId', data.userId);
      localStorage.setItem('username', data.username)
      localStorage.setItem('sessionId', 'mock-session-id');
      window.location.href = 'landingPage.html';
    } catch (error) {
      alert('User creation failed');
      console.error('Error during user creation:', error);
    }
  });
});
