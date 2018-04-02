package poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Poker {

	private Card[] trump = new Card[52];//トランプの配列
	private char[] mark = new char[]{'♣','♦','❤','♤'};//マークの配列
	private Random r = new Random();//ランダムクラスを作成
	private ArrayList<Card> hand = new ArrayList<Card>();//手札リスト
	private int stockCount;//山から引いたトランプの枚数カウント
	private Scanner sc = new Scanner(System.in);//入力クラスを作成
	private Coin coin = new Coin();
	private int betCoinAmount;//賭けたコインの枚数
	private int gainCoinAmount;//得たコインの枚数
	private int doubleUpSuccess;//ダブルアップの結果を入れる変数

	//コンストラクタ
	public Poker(){
	}

	//ポーカーを始めるか確認するメソッド
	public void start(){
		int startButton;
		while(true){
			System.out.println("現在のコインは" + coin.getCoin() + "枚です");
			System.out.println("ポーカーをスタートする場合は1、終了する場合は0を入力してください");
			if(sc.hasNextInt()){
				startButton = sc.nextInt();
				if(startButton == 1){
					run();
				}else if(startButton == 0){
					break;
				}else{
					continue;
				}
			}else {
				sc.next();
			}
		}
		sc.close();
		finish();
	}


	//ポーカーを行うメソッド
	public void run(){
		ready();
		makeTrump();
		shuffle();
		deal();
		showHand();
		betCoin();
		select();
		result();
		judge();
		//コインを獲得した場合ダブルアップをするか決めるメソッドに進む
		if(gainCoinAmount > 0){
			doubleUpDicide();
		}
	}

	//トランプの山を作るメソッド
	public void makeTrump(){
		for(int i = 0; i < 52; i++){
			trump[i] = new Card();
			trump[i].setNumber(i % 13 + 1);
			trump[i].setMark(mark[i / 13]);
		}
	}

	//全てのトランプを表示するメソッド
	public void allTrump(){
		for(int i = 0; i < 52; i++){
			System.out.print(trump[i].getMark());
			System.out.println(trump[i].getNumber());
		}
	}

	//トランプの山をシャッフルするメソッド
	public void shuffle(){
		Card t = new Card();//一旦入れるカードクラス
		int c1,c2;
		for(int i = 0; i < 100; i++){
			c1 = r.nextInt(52);
			c2 = r.nextInt(52);
			t = trump[c1];//1を一旦tに入れる
			trump[c1] = trump[c2];//2を1に入れる
			trump[c2] = t;//tを2に入れる
		}
	}

	//手札にトランプを5枚配るメソッド
	public void deal(){
		for(int i = 0 ; i < 5 ; i++){
			hand.add(trump[i]);
			stockCount += 5;//山から５枚引く
		}
	}

	//手札を表示するメソッド
	public void showHand(){
		System.out.println("あなたに配られたカードです。");
		for(int i = 0 ; i < hand.size(); i++){
			System.out.print(i + ":");
			System.out.print(hand.get(i).getMark());
			System.out.println(hand.get(i).getNumber());
		}
	}

	//ベットするコインの枚数を入力させるメソッド
	public void betCoin(){
		while(true){
			System.out.println("ベットするコインの枚数を入力してください");
			if(sc.hasNextInt()){
				betCoinAmount = sc.nextInt();
				if(betCoinAmount > 0 && betCoinAmount <= coin.getCoin()){
					coin.setCoin(coin.getCoin() - betCoinAmount);
					break;
				}else if(betCoinAmount > coin.getCoin()){
					System.out.println("そんなにコインを持っていません");
					continue;
				}else{
					continue;
				}
			}else{
				sc.next();
			}
		}
	}


	//手札から捨てるカードを選ぶメソッド
	public void select(){
		System.out.println("捨てるカードの番号(0~4)を入力してください(複数可)");
		System.out.println("判定に進む場合には5を入力してください");
		List<Integer> changeNumber = new ArrayList<Integer>();//一度交換したカードの番号を入れるリスト
		while(true){
			if(sc.hasNextInt()){
				int discard = sc.nextInt();//捨てるカードの番号を入力させる
				if(changeNumber.indexOf(discard) >= 0){
					System.out.println("一度交換したカードは交換できません");
				}else if(discard == 5){
					break;//ループを抜けて判定に進む
				}else if(discard >= 0 && discard <= 4){
					hand.set(discard,trump[stockCount]);//トランプの山の一番上のカードを手札の捨てたカードの位置に置く
					stockCount++;//トランプの山から一枚捨てる
					changeNumber.add(discard);//交換した番号を記録
				}else{
					System.out.println("0~5の数値を入力してください");
				}
			}else{
				System.out.println("数値を入力してください");
				sc.next();
			}
		}
	}

	//最終的な手札のカードを表示するメソッド
	public void result(){
		System.out.println("結果はこちらです");
		for(int i = 0 ; i < hand.size(); i++){
			System.out.print(hand.get(i).getMark());
			System.out.println(hand.get(i).getNumber());
		}
	}

	//役を判定するメソッド
	public void judge(){
		if(judgeFlush() && judgeRoyal()){
			System.out.println("ロイヤルストレートフラッシュです!!!!!!!!!");
			gainCoin(100);
		}else if(judgeStraight() && judgeFlush()){
			System.out.println("ストレートフラッシュです!!!!!!!!");
			gainCoin(50);
		}else if(judgeFourCard()){
			System.out.println("フォーカードです!!!!!!!");
			gainCoin(20);
		}else if(judgeFullHouse()){
			System.out.println("フルハウスです!!!!!!");
			gainCoin(7);
		}else if(judgeFlush()){
			System.out.println("フラッシュです!!!!!");
			gainCoin(5);
		}else if(judgeStraight()){
			System.out.println("ストレートです!!!!");
			gainCoin(4);
		}else if(judgeThreeCard()){
			System.out.println("スリーカードです!!!");
			gainCoin(3);
		}else if(judgeTwoPair()){
			System.out.println("ツーペアです!!");
			gainCoin(2);
		}else if(judgeOnePair()){
			System.out.println("ワンペアです!");
			gainCoin(1);
		}else{
			System.out.println("ノーペアです");
			gainCoin(0);
		}
	}

	//フラッシュ判定メソッド
	public boolean judgeFlush(){
		char markCheckBox;
		markCheckBox = hand.get(0).getMark();//手札の0枚目のマークをBoxに入れる
		//他の4枚をそのマークと比べ、全て同じだった場合trueを返す
		for(int i = 1 ; i <= 5 ; i++){
			if(i == 5){
				return true;
			}else if(markCheckBox == hand.get(i).getMark()){
				continue;
			}else {
				break;
			}
		}
		return false;
	}

	//ロイヤル(10,11,12,13,1)判定メソッド
	public boolean judgeRoyal(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		//ロイヤルの場合trueを返す
		if(numberList[0] == 1 && numberList[1] == 10 && numberList[2] == 11 &&
				numberList[3] == 12 && numberList[4] == 13){
			return true;
		}else{
			return false;
		}
	}

	//ストレート判定メソッド
	public boolean judgeStraight(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		//13と1をまたぐ時のための補正
		//例えば1,2,11,12,13の場合14,15,11,12,13、1,2,3,4,5の場合14,15,16,17,18に変更
		for(int i = 0 , number = 1 ; i < 5 ; i++ , number++){
			if(numberList[i] == number){
				numberList[i] += 13;
			}else{
				break;
			}
		}
		Arrays.sort(numberList);//もう一度数字を昇順でソート
		//全て連続した数字だった場合trueを返す
		if(numberList[0] == (numberList[1] -1) && numberList[0] == (numberList[2] -2) &&
			numberList[0] == (numberList[3] -3) && numberList[0] == (numberList[4] -4) ){
			return true;
		}else{
			return false;
		}
	}

	//フォーカード判定メソッド
	public boolean judgeFourCard(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		if(numberList[0] == numberList[1] && numberList[1] == numberList[2] &&
				numberList[2] == numberList[3]){
			return true;//0~3まで同じ数字だった場合true
		}else if(numberList[1] == numberList[2] && numberList[2] == numberList[3] &&
				numberList[3] == numberList[4]){
			return true;//1~4まで同じ数字だった場合true
		}else{
			return false;
		}
	}

	//フルハウス判定メソッド
	public boolean judgeFullHouse(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		if(numberList[0] == numberList[1] && numberList[2] == numberList[3] &&
				numberList[3] == numberList[4]){
			return true;//0と1、2~4が同じ数字だった場合true
		}else if(numberList[0] == numberList[1] && numberList[1] == numberList[2] &&
				numberList[3] == numberList[4]){
			return true;//0~2、3と4が同じ数字だった場合true
		}else{
			return false;
		}
	}
	//スリーカード判定メソッド
	public boolean judgeThreeCard(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		if(numberList[0] == numberList[1] && numberList[1] == numberList[2]){
			return true;//0と1と2が同じ数字だった場合true
		}else if(numberList[1] == numberList[2] && numberList[2] == numberList[3]){
			return true;//1と2と3が同じ数字だった場合true
		}else if(numberList[2] == numberList[3] && numberList[3] == numberList[4]){
			return true;//2と3と4が同じ数字だった場合true
		}else{
			return false;
		}
	}

	//ツーペア判定メソッド
	public boolean judgeTwoPair(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		if(numberList[0] == numberList[1] && numberList[2] == numberList[3]){
			return true;//0と1、2と3が同じ数字だった場合true
		}else if(numberList[0] == numberList[1] && numberList[3] == numberList[4]){
			return true;//0と1、3と4が同じ数字だった場合true
		}else if(numberList[1] == numberList[2] && numberList[3] == numberList[4]){
			return true;//1と2、3と4が同じ数字だった場合true
		}else{
			return false;
		}
	}

	//ワンペア判定メソッド
	public boolean judgeOnePair(){
		int[] numberList = new int[5];//カード５枚の数字を入れる配列
		//数字だけ取り出し配列に入れる
		for(int i = 0 ; i < 5 ; i++){
			numberList[i] = hand.get(i).getNumber();
		}
		Arrays.sort(numberList);//数字を昇順でソート
		//0と1または1と2または2と3または3と4が同じ数字だった場合true
		if(numberList[0] == numberList[1] || numberList[1] == numberList[2] ||
				numberList[2] == numberList[3] || numberList[3] == numberList[4]){
			return true;
		}else{
			return false;
		}
	}

	//役によっての倍率を引数にして儲けたコインの枚数を計算して表示するメソッド
	public void gainCoin(int magnification){
		gainCoinAmount = betCoinAmount * magnification ;
		System.out.println(gainCoinAmount + "枚のコインをゲットしました");
	}

	//ゲームの準備を行うメソッド
	public void ready(){
		stockCount = 0;
		hand.clear();//手札を削除する
		betCoinAmount = 0;
		gainCoinAmount = 0;
	}

	//ポーカーを終了するメソッド
	public void finish(){
		System.out.println("ポーカーを終了しました。");
		//コインの残り枚数を表示
		System.out.println("最終コインは" + coin.getCoin() + "枚です。");
	}

	//ダブルアップに挑戦するか決めるメソッド
	public void doubleUpDicide(){
		while(true){
			System.out.println("ダブルアップに挑戦しますか？");
			System.out.println("する→1、しない→0、を入力してください");
			if(sc.hasNextInt()){
				int dicision = sc.nextInt();
				if(dicision == 1){
					doubleUpGame();//ダブルアップゲームに進む
					if(doubleUpSuccess == 1){
						continue;//成功した場合もう一度するか決める
					}else if(doubleUpSuccess == 2){
						break;//失敗した場合メソッドを終了する
					}else if(doubleUpSuccess == 3){
						continue;//同じ数字が出た場合もう一度するか決める
					}
				}else if(dicision == 0){
					coin.setCoin(coin.getCoin() + gainCoinAmount);
					break;//しない場合得たコインを所持コインに加えて終了する
				}else{
					continue;//1か0以外の数値を入力した場合もう一度挑戦するか聞く
				}
			}else{
				System.out.println("1か0を入力してください");
				sc.next();//文字などを入力した場合、もう一度繰り返す
			}
		}
	}

	//ダブルアップチャンスメソッド
	public void doubleUpGame(){
		Card basicCard = new Card();//基準のカードを入れるクラスを作成
		basicCard = trump[stockCount];//トランプの山の一番上のカードを代入
		stockCount ++ ;//トランプの山を一枚減らす
		System.out.print(basicCard.getMark());
		System.out.println(basicCard.getNumber());//基準のカードの絵柄と数字を表示
		Card chanceCard = new Card();//
		chanceCard = trump[stockCount];
		while(true){
			System.out.println("high→1、low→0、を入力してください");
			if(sc.hasNextInt()){
				int highlow = sc.nextInt();
				if(highlow == 1){
					System.out.print(chanceCard.getMark());
					System.out.println(chanceCard.getNumber());
					if(basicCard.getNumber() < chanceCard.getNumber()){
						System.out.println("ダブルアップ成功です!!!!");
						gainCoinAmount *= 2;
						System.out.println("コインが" + gainCoinAmount + "枚になりました!");
						doubleUpSuccess = 1;//成功の場合1を代入
						break;
					}else if(basicCard.getNumber() > chanceCard.getNumber()){
						System.out.println("残念でした");
						gainCoinAmount = 0;
						System.out.println("コインが" + gainCoinAmount + "枚になりました!");
						doubleUpSuccess = 2;//失敗の場合2を代入
						break;
					}else if(basicCard.getNumber() == chanceCard.getNumber()){
						System.out.println("数字が同じでした");
						doubleUpSuccess = 3;//数字が同じでやり直しの場合3を代入
						break;
					}
				}else if(highlow == 0){
					System.out.print(chanceCard.getMark());
					System.out.println(chanceCard.getNumber());
					if(basicCard.getNumber() > chanceCard.getNumber()){
						System.out.println("ダブルアップ成功です!!!!");
						gainCoinAmount *= 2;
						System.out.println("コインが" + gainCoinAmount + "枚になりました!");
						doubleUpSuccess = 1;//成功の場合1を代入
						break;
					}else if(basicCard.getNumber() < chanceCard.getNumber()){
						System.out.println("残念でした");
						gainCoinAmount = 0;
						System.out.println("コインが" + gainCoinAmount + "枚になりました!");
						doubleUpSuccess = 2;//失敗の場合2を代入
						break;
					}else if(basicCard.getNumber() == chanceCard.getNumber()){
						System.out.println("数字が同じでした");
						doubleUpSuccess = 3;//数字が同じでやり直しの場合3を代入
						break;
					}
				}else{
					continue;
				}
			}else{
				System.out.println("1か0を入力してください");
				sc.next();
			}
		}
	}




}
