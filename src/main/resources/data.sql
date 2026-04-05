DELETE FROM mentorship_sessions WHERE mentor_id >= 1 OR mentee_id >= 1;
DELETE FROM user_skills WHERE user_id >= 1;
DELETE FROM user_interests WHERE user_id >= 1;
DELETE FROM user_profiles WHERE id >= 1;

ALTER TABLE user_profiles ALTER COLUMN role ENUM('MENTOR', 'MENTEE', 'ADMIN');

MERGE INTO user_profiles (id, available_for_mentorship, bio, email, full_name, password_hash, role, years_of_experience) KEY(id) VALUES
  (1, true, 'Management consulting mentor focused on strategy and client communication.', 'riya.mentor@example.com', 'Riya Patil', null, 'MENTOR', 8),
  (2, true, 'Business consulting mentor for operations improvement and stakeholder management.', 'sneha.mentor@example.com', 'Sneha Joshi', null, 'MENTOR', 6),
  (3, true, 'Mentee looking for guidance in consulting career growth and case interview preparation.', 'aman.mentee@example.com', 'Aman Kulkarni', null, 'MENTEE', 1),
  (4, true, 'Platform admin for profile moderation and data management.', 'admin@example.com', 'System Admin', null, 'ADMIN', 10);

MERGE INTO user_skills KEY(user_id, skill) VALUES
  (1, 'market analysis'),
  (1, 'client communication'),
  (1, 'business strategy'),
  (2, 'operations consulting'),
  (2, 'stakeholder management'),
  (2, 'case solving'),
  (3, 'business analysis'),
  (3, 'presentation skills');

MERGE INTO user_interests KEY(user_id, interest) VALUES
  (1, 'consulting careers'),
  (1, 'case interviews'),
  (2, 'leadership coaching'),
  (2, 'career transitions'),
  (3, 'case interviews'),
  (3, 'consulting careers');

ALTER TABLE user_profiles ALTER COLUMN id RESTART WITH 5;
