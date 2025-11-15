export interface AddressRequestDto {
  fullName: string;
  line1: string;
  line2?: string;      // optional because many times users may not enter it
  city: string;
  state: string;
  country: string;
  postalCode: string;
  phone: string;
  isDefault?: boolean; // optional if backend handles defaulting
  type: string;        // e.g., Home, Work, Other
}
