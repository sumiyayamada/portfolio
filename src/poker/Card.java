package poker;

public class Card {

	//フィールド
	private char mark;//絵柄
	private int number;//数字

	//コンストラクタ
	public Card(){

	}

	//getter/setter
	public char getMark() {
		return mark;
	}

	public void setMark(char mark) {
		this.mark = mark;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}


}
