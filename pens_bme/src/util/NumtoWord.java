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
							WBT = "หนึ่ง" + WBT;
						} else {
							WBT = chkNumber(String.valueOf(BT.charAt(I))) + WBT;
						}
					}
					break;
				case 2:
					switch (Integer.parseInt(String.valueOf(BT.charAt(I)))) {
					case 2:
						WBT = "ยี่สิบ" + WBT;
						break;
					case 1:
						WBT = "สิบ" + WBT;
						break;
					default:
						if (Integer.parseInt(String.valueOf(BT.charAt(I))) > 0) {
							WBT = chkNumber(String.valueOf(BT.charAt(I))) + "สิบ" + WBT;
						}
						break;
					}
					break;
				case 3:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "ร้อย" + WBT;
					}
					break;
				case 4:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "พัน" + WBT;
					}
					break;
				case 5:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "หมื่น" + WBT;
					}
					break;
				case 6:
					if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "แสน" + WBT;
					}
					break;
				case 7:
					if (((LBT - 1) % 6) == 0) {
						WBT = chkNumber(String.valueOf(BT.charAt(I))) + "ล้าน" + WBT;
					} else {
						if (Integer.parseInt(String.valueOf(BT.charAt(I - 1))) > 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) == 1) {
							WBT = "หนึ่งล้าน" + WBT;
						}
						if (Integer.parseInt(String.valueOf(BT.charAt(I))) != 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) != 1) {
							WBT = chkNumber(String.valueOf(BT.charAt(I))) + "ล้าน" + WBT;
						}
						if (Integer.parseInt(String.valueOf(BT.charAt(I))) == 0
								&& Integer.parseInt(String.valueOf(BT.charAt(I))) != 1) {
							WBT = "ล้าน" + WBT;
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
								WST = "หนึ่ง" + WST;
							} else {
								WST = chkNumber(String.valueOf(ST.charAt(I))) + WST;
							}
						}
						break;
					case 2:
						switch (Integer.parseInt(String.valueOf(ST.charAt(I)))) {
						case 2:
							WST = "ยี่สิบ" + WST;
							break;
						case 1:
							WST = "สิบ" + WST;
							break;
						default:
							if (Integer.parseInt(String.valueOf(ST.charAt(I))) > 0) {
								WST = chkNumber(String.valueOf(ST.charAt(I))) + "สิบ" + WST;
							}
							break;
						}
						break;
					case 3:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "ร้อย" + WST;
						}
						break;
					case 4:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "พัน" + WST;
						}
						break;
					case 5:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "หมื่น" + WST;
						}
						break;
					case 6:
						if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "แสน" + WST;
						}
						break;
					case 7:
						if (((ST.length() - 1) % 6) == 0) {
							WST = chkNumber(String.valueOf(ST.charAt(I))) + "ล้าน" + WST;
						} else {
							if (Integer.parseInt(String.valueOf(ST.charAt(I - 1))) > 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) == 1) {
								WST = "หนึ่งล้าน" + WST;
							}
							if (Integer.parseInt(String.valueOf(ST.charAt(I))) != 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) != 1) {
								WST = chkNumber(String.valueOf(ST.charAt(I))) + "ล้าน" + WST;
							}
							if (Integer.parseInt(String.valueOf(ST.charAt(I))) == 0
									&& Integer.parseInt(String.valueOf(ST.charAt(I))) != 1) {
								WST = "ล้าน" + WST;
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
			WBT = WBT + "บาท";
		}
		if (WST.length() > 0) {
			WST = WST + "สตางค์";
		} else {
			WBT = WBT + "ถ้วน";
		}
		return WBT + WST;
	}

	private String chkNumber(String X) {
		String ChNumber = "";
		switch (Integer.parseInt(X)) {
		case 1:
			ChNumber = "หนึ่ง";
			break;
		case 2:
			ChNumber = "สอง";
			break;
		case 3:
			ChNumber = "สาม";
			break;
		case 4:
			ChNumber = "สี่";
			break;
		case 5:
			ChNumber = "ห้า";
			break;
		case 6:
			ChNumber = "หก";
			break;
		case 7:
			ChNumber = "เจ็ด";
			break;
		case 8:
			ChNumber = "แปด";
			break;
		case 9:
			ChNumber = "เก้า";
			break;
		}
		return ChNumber;
	}

}
