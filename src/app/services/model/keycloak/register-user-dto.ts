export interface RegisterUserDto {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  attributes?: {
    phoneNumber?: string[];
    address?: string[];
    locale?: string[];
  };
  credentials: {
    type: string;
    value: string;
    temporary: boolean;
  }[];
}
