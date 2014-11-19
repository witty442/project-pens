package util;

public class NumtoWord {

	public String numToWord(Double dSNX) {
		String SNX = String.valueOf(dSNX);
		int LBT, LX, I, SEQ;
		String ST = "", BT = "", WBT = "", WST = "";

		LX = SNX.length();
		for (I = 1; I <= LX; I++) {
			if (SNX.charAt(I) == '.') {
				BT = SNX.substring(0, I);
				ST = SNX.substring(I + 1, SNX.length());
				break;
			}
		}
		I = I - LX;
		if (BT.trim().length() == 0) {
			BT = SNX;
		}
		SEQ = 1;
		LBT = BT.length();
		for (I = LBT - 1; I >= 0; I--) {
			if (LBT == 1) {
				WBT = chkNumber(String.valueOf(BT.charAt(I))) + WBT;
			} else {
				switch (SEQ) {
				case 1:
					if ((I - 1) > -1) {
						if (Integer.parseInt(String.valueOf(BT.charAt(I - 1))) > 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) == 1) {
							WBT = "˹��" + WBT;
						} else {
							WBT = chkNumber(String.valueOf(BT.charAt(I))) + WBT;
						}
					}
					break;
				case 2:
					switch (Integer.parseInt(String.valueOf(BT.charAt(I)))) {
					case 2:
						WBT = "����Ժ" + WBT;
						break;
					case 1:
						WBT = "�Ժ" + WBT;
						break;
					default:
						if (Integer.parseInt(String.valueOf(BT.charAt(I))) > 0) {
							WBT = chkNumber(String.valueOf(BT.charAt(I))) + "�Ժ" + WBT;
						}
						break;
					}
					break;
				case 3:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "����" + WBT;
					}
					break;
				case 4:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "�ѹ" + WBT;
					}
					break;
				case 5:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "����" + WBT;
					}
					break;
				case 6:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "�ʹ" + WBT;
					}
					break;
				case 7:
					if (((LBT - 1) % 6) == 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "��ҹ" + WBT;
					} else {
						if (Integer.parseInt(String.valueOf(BT.charAt(I - 1))) > 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) == 1) {
							WBT = "˹����ҹ" + WBT;
						}
						if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) != 1) {
							WBT = chkNumber(String.valueOf(BT.charAt(I))) + "��ҹ" + WBT;
						}
						if (Integer.parseInt(String.valueOf(BT.charAt(I))) == 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) != 1) {
							WBT = "��ҹ" + WBT;
						}
					}
					SEQ = 1;
					break;
				}
				SEQ = SEQ + 1;
			}
		}

		if (ST.trim().length() == 0) {
			ST = "00";
		}
		SEQ = 1;
		if (Integer.parseInt(ST) != 0) {
			for (I = ST.length() - 1; I >= 0; I--) {
				if (ST.length() == 1) {
					WST = chkNumber(String.valueOf(BT.charAt(I))) + WST;
				} else {
					switch (SEQ) {
					case 1:
						if ((I - 1) > -1) {
							if (Integer.parseInt(String.valueOf(ST.charAt(I - 1))) > 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) == 1) {
								WST = "˹��" + WST;
							} else {
								WST = chkNumber(String.valueOf(ST.charAt(I))) + WST;
							}
						}
						break;
					case 2:
						switch (Integer.parseInt(String.valueOf(ST.charAt(I)))) {
						case 2:
							WST = "����Ժ" + WST;
							break;
						case 1:
							WST = "�Ժ" + WST;
							break;
						default:
							if (Integer.parseInt(String.valueOf(ST.charAt(I))) > 0) {
								WST = chkNumber(String.valueOf(ST.charAt(I))) + "�Ժ" + WST;
							}
							break;
						}
						break;
					case 3:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "����" + WST;
						}
						break;
					case 4:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "�ѹ" + WST;
						}
						break;
					case 5:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "����" + WST;
						}
						break;
					case 6:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "�ʹ" + WST;
						}
						break;
					case 7:
						if (((ST.length() - 1) % 6) == 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "��ҹ" + WST;
						} else {
							if (Integer.parseInt(String.valueOf(ST.charAt(I - 1))) > 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) == 1) {
								WST = "˹����ҹ" + WST;
							}
							if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) != 1) {
								WST = chkNumber(String.valueOf(ST.charAt(I))) + "��ҹ" + WST;
							}
							if (Integer.parseInt(String.valueOf(ST.charAt(I))) == 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) != 1) {
								WST = "��ҹ" + WST;
							}
						}
						SEQ = 1;
						break;
					}
					SEQ = SEQ + 1;
				}
			}
		}
		if (WBT.length() > 0) {
			WBT = WBT + "�ҷ";
		}
		if (WST.length() > 0) {
			WST = WST + "ʵҧ��";
		} else {
			WBT = WBT + "��ǹ";
		}
		return WBT + WST;
	}

	private String chkNumber(String X) {
		String ChNumber = "";
		switch (Integer.parseInt(X)) {
		case 1:
			ChNumber = "˹��";
			break;
		case 2:
			ChNumber = "�ͧ";
			break;
		case 3:
			ChNumber = "���";
			break;
		case 4:
			ChNumber = "���";
			break;
		case 5:
			ChNumber = "���";
			break;
		case 6:
			ChNumber = "ˡ";
			break;
		case 7:
			ChNumber = "��";
			break;
		case 8:
			ChNumber = "Ỵ";
			break;
		case 9:
			ChNumber = "���";
			break;
		}
		return ChNumber;
	}

}
