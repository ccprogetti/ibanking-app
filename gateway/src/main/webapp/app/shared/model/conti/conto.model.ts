export interface IConto {
  id?: number;
  nome?: string;
  iban?: string | null;
  userName?: string;
  abi?: string;
}

export const defaultValue: Readonly<IConto> = {};
