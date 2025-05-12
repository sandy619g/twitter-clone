import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import HomePage from './components/HomePage';
import FeedPage from './components/FeedPage';
import CreateUserPage from './components/CreateUser';
import UserProfilePage from './components/UserProfile';
import EditProfile from './components/EditProfile';

function App() {
  return (
    <Router>
      <nav style={{ padding: '1rem', background: '#0077b6', color: 'white' }}>
        <Link to="/" style={{ marginRight: 20, color: 'white' }}>Home</Link>
        <Link to="/feed" style={{ marginRight: 20, color: 'white' }}>Feed</Link>
        <Link to="/create-user" style={{ color: 'white' }}>Create User</Link>
      </nav>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/feed" element={<FeedPage />} />
        <Route path="/create-user" element={<CreateUserPage />} />
        <Route path="/profile/:id" element={<UserProfilePage />} />
        <Route path="/user/:id" element={<UserProfilePage />} />
        <Route path="/edit-profile/:id" element={<EditProfile />} />
      </Routes>
    </Router>
  );
}

export default App;
