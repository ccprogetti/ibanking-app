export interface IConto {
  id?: number;
  nome?: string;
  iban?: string | null;
  userName?: string;
  abi?: string;
}

export class Conto implements IConto {
  constructor(public id?: number, public nome?: string, public iban?: string | null, public userName?: string, public abi?: string) {}
}

export function getContoIdentifier(conto: IConto): number | undefined {
  return conto.id;
}
