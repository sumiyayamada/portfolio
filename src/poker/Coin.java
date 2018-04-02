package poker;

//コインの管理をするクラス
public class Coin {
	
	private int coin;//コインの枚数を保持するフィールド
	
	//コンストラクタ
	public Coin(){
		coin = 100;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}
	
	
	
	

}
