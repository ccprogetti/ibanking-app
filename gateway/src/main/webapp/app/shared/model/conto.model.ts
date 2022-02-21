export interface IConto {
  id?: number;
  nome?: string;
  iban?: string | null;
}

export const defaultValue: Readonly<IConto> = {};
