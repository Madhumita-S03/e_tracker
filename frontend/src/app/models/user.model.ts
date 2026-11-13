export interface FamilyMember {
  id: number;
  username: string;
  fullName: string;
  avatarColor: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  username: string;
  fullName: string;
  role: string;
  avatarColor: string;
}

export interface AuthUser {
  userId: number;
  username: string;
  fullName: string;
  role: string;
  avatarColor: string;
}
