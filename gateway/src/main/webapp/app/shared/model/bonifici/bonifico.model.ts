import dayjs from 'dayjs';

export interface IBonifico {
  id?: number;
  causale?: string;
  destinatario?: string;
  importo?: number | null;
  dataEsecuzione?: string | null;
  ibanDestinatario?: string | null;
}

export const defaultValue: Readonly<IBonifico> = {};
