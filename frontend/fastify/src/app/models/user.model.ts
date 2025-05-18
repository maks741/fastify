export interface User {
  id: string;
  username: string;
  email: string;
  token: string;
}

export interface SignupData {
  username: string;
  email: string;
  password: string;
}

export interface LoginData {
  email: string;
  password: string;
}
