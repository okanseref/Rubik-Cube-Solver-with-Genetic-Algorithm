package yapayzekaproje;

import java.awt.Dimension;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;



public class main {
	static float angleSkor=0; // A�� de�erleri toplam�
	static int STARTLEN=4; // Ba�lang�� dizi boyutumuz
	static int ShuffleCount=50; //K�p� ka� kere kar��t�raca��m�z
	static int tolerans=3; //Teori de�erlendirme fonk. i�in tolerans de�eri

	static int maxDenemeSayisi=3000; //maksimum iterasyon say�s�
	static int P=200;
	static double degisimOrani; //mutasyon oran�
	static int[][][] cubeStart; 
	
	
	static List<Integer> paths; // �uanki de�erler dizisi
	static List<Integer> solution; // En iyi de�er
	static double bestSkor;
	static double yerelMaxSkor=0;

	static List<Integer> yerelMaxSolution; //Yerel maksimum de�eri
	static List<Double> chartValues; //Grafik yazd�rmak i�in de�erleri tutar
	static double cur_skor; //Mutasyonda �nceki de�erle kar��la�t�rmak i�in de�erler
	static double prev_skor;
	static int[] moves= {0,1,2,3,4,5,6,7,8,9,10,11};

	public static void main(String[] args) {
		cubeStart=new int[6][3][3];
		List<Integer> totalSolution= new ArrayList<>();
		int process=0;
		int[][][] cubeBase=new int[6][3][3];

		int i,j,k,counter=0;
		chartValues=new ArrayList<>();
		paths = new ArrayList<>();
		Random r = new Random();
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					cubeStart[i][j][k]=i; // d�zg�n k�p her y�zeyi ayn� renk
				}
			}
		}

		List<List<Integer>> populasyon= new ArrayList<>();
		List<List<Integer>> populasyonNew= new ArrayList<>();
		
		int nesilSayisi=16,populasyonSayisi=10; //Nesil ve pop�lasyon de�erleri
		cubeStart=Shuffle(cubeStart);
		cubeBase=cubeStart.clone();
		for (k = 0; k < populasyonSayisi; k++) { //Rastgele ba�lang�� yarat�l�yor
			List<Integer> temp = new ArrayList<>();
			temp=new ArrayList<>(RandomSolution(false));
			populasyon.add(new ArrayList<>(temp));
			paths=populasyon.get(k);

		}

		PrintCube(cubeStart);
		solution=new ArrayList<>(paths);
		yerelMaxSolution=new ArrayList<>(paths);
		
        LineDrawing ld = new LineDrawing(cubeStart,"Start"); //Ba�lang�� k�p�n� �izdiriyoruz
        ld.setVisible(true);
        
		for (int nesil = 1; nesil < nesilSayisi; nesil++) {
			for (k = 0; k < populasyonSayisi; k++) {
				//Pop�lasyonun her bir bireyine mutasyonlar uygulan�r
				paths=new ArrayList<>(populasyon.get(k));
				bestSkor=Skor(paths,cubeStart);
				solution=new ArrayList<>(paths);

				yerelMaxSkor=bestSkor;
				yerelMaxSolution=new ArrayList<>(paths);

				i=0;
				int z=0;
				while ( z <maxDenemeSayisi) {
					i=0;
					degisimOrani=0.16f; // mutasyon ba�lang�� oran�

					while (i<P) {
						z++;
						paths=new ArrayList<>(yerelMaxSolution);
						counter++;
						Mutation(degisimOrani); //Mutasyon oran�nda paths'e mutasyon uygulan�r
						cur_skor=Skor(paths,cubeStart);
						if(cur_skor>yerelMaxSkor) {
							yerelMaxSkor=cur_skor;
							yerelMaxSolution=new ArrayList<>(paths);
						}
						if(cur_skor>prev_skor) {
							degisimOrani*=0.98f; //K�t�le�tiyse de�er mutasyon artar
						}else {
							degisimOrani*=1.02f;
							if(degisimOrani>0.8) { // Mutasyon 0.8 i ge�emez
								degisimOrani=0.8;
								//System.out.println("GE�T�!!!!!!!!!!");
							}
						}
						prev_skor=cur_skor;
						i++;
					}
					if(yerelMaxSkor>bestSkor) {
						bestSkor=yerelMaxSkor;
						solution=new ArrayList<>(yerelMaxSolution);
					}
					z++;
				}
				populasyonNew.add(solution); // Olu�an en iyi durumu birey olarak ekliyoruz
			}
			populasyon.clear();

			float best=Skor(populasyonNew.get(0),cubeStart); // en iyi bireyin indeksini buluyoruz
			int bestIndex=0;
			for (int l = 0; l < populasyonNew.size(); l++) {
				if(Skor(populasyonNew.get(l),cubeStart)>best) {
					bestIndex=l;
				}

			}
			chartValues.add((double)Skor(populasyonNew.get(bestIndex),cubeStart)); //Neslin en iyi bireyine grafi�e ekliyoruz

			populasyon.add(populasyonNew.get(bestIndex)); //En iyi bireyi gelecek nesle 2 kez ekliyoruz
			for (int l = 1; l < populasyonNew.size(); l++) {

				if(l<populasyonNew.size()/4) { //Rastgele bireyler ekliyoruz
					populasyon.add(populasyonNew.get(r.nextInt(populasyonSayisi)));
				}else {
					//Yar�s� da crossoverdan geliyor
					populasyon.add(CrossOver(populasyonNew.get(r.nextInt(populasyonNew.size())), populasyonNew.get(r.nextInt(populasyonNew.size()))));
				}
			}
			System.out.print("Se�ilen birey: Nesil "+nesil+" Birey "+bestIndex+"/"+populasyon.size()+" : "+Skor(populasyonNew.get(bestIndex),cubeStart)+"/54 \n");
			System.out.println(populasyonNew.get(bestIndex));

			if(bestIndex>=populasyonNew.size()/4) {
				System.out.println("Cross Overdan birey elde edildi");
			}
			solution= new ArrayList<>(populasyonNew.get(bestIndex));
			
			//�nceki pathi kaydedip �imdiki haline yeni ��z�m dizisi ar�yoruz
			
			if(Skor(populasyonNew.get(bestIndex),cubeStart)>process) {
				process=Skor(populasyonNew.get(bestIndex),cubeStart);
				for(i=0;i<solution.size();i++) {
					cubeStart=Slide(cubeStart,solution.get(i));
					totalSolution.add(solution.get(i));
				}
				solution.clear();
				for(int l=0;l<STARTLEN*3;l++) {//sabit duran bir ��z�m ekleyelim 
					solution.add(l%2);
				}
				populasyon.clear();
				populasyon.add(solution);

				for(int l=1;l<populasyonSayisi;l++) {
					populasyon.add(RandomSolution(true));
				}
			}
			/////
			populasyonSayisi+=4;
			for(int l=0;l<4;l++) {//yeni random bireyler ekliyoruz
				populasyon.add(RandomSolution(true));
			}
			PrintCubeWithMoves(cubeBase, totalSolution); //Checkpointli ise totalsolution olacak

			
			populasyonNew.clear();
		}
		totalSolution=Crop(totalSolution); //Checkpointli ise totalsolution olacak
		System.out.println("Counter:"+counter);
		System.out.println(totalSolution);
		System.out.println("SkorU:"+SkorStandart(totalSolution,cubeBase));
		for(i=0;i<totalSolution.size();i++) {
			cubeBase=Slide(cubeBase,totalSolution.get(i));
		}
		PrintCube(cubeBase);
		
		DrawChart(chartValues,cubeBase);

	}
	private static void PrintCube(int[][][] cube) {
		int i,j,k;
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					System.out.print(cube[i][j][k]+" ");

				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
	}
	private static List<Integer> Crop(List<Integer> val) {
		int i;
		i=1;
		while(i<val.size()) {
			if((val.get(i)%2==1&&val.get(i)-1==val.get(i-1))) {
				val.remove(i-1);
				val.remove(i-1);
				i--;
			}
			i++;
		}
		return val;
	}
	private static void PrintCubeWithMoves(int[][][] cube,List<Integer> val) {
		int i,j,k;
		for(i=0;i<val.size();i++) {
			cube=Slide(cube,val.get(i));
		}
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					System.out.print(cube[i][j][k]+" ");

				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
	}
	private static int Skor(List<Integer> val,int[][][] start) {
		int selection=1;
		switch(selection) {
		case 0:
			return SkorTeori(val,start);
		case 1:
			return SkorStandart(val,start);
		}
		return 0;
	}
	private static int SkorTeori(List<Integer> val,int[][][] start) {
		int skor=6;
		int i,k;
		for(i=0;i<val.size();i++) {
			start=Slide(start,val.get(i));
		}
		k=start[4][1][1];
		if(start[4][1][0]==k) {
			skor++;
		}
		if(start[4][0][1]==k) {
			skor++;
		}
		if(start[4][1][2]==k) {
			skor++;
		}
		if(start[4][2][1]==k) {
			skor++;
		}
		if(skor==10) { // �i�ek �ekli olu�tuysa
			if(start[0][0][1]==start[0][1][1]) { //orta �st ve orta e�itse
				skor++;
			}
			if(start[1][0][1]==start[1][1][1]) {
				skor++;
			}
			if(start[2][0][1]==start[2][1][1]) {
				skor++;
			}
			if(start[3][0][1]==start[3][1][1]) {
				skor++;
			}
			if(skor==14) {
				if(start[0][0][2]==start[0][1][1]) {
					skor++;
				}
				if(start[1][0][0]==start[1][1][1]) { //�st yanlara bak�yoruz
					skor++;
				}
				if(start[0][0][0]==start[0][1][1]) { //�st yanlara bak�yoruz
					skor++;
				}
				if(start[3][0][2]==start[3][1][1]) {
					skor++;
				}

				if(start[1][0][2]==start[1][1][1]) {
					skor++;
				}
				if(start[2][0][0]==start[2][1][1]) { //�st yanlara bak�yoruz
					skor++;
				}
				if(start[2][0][2]==start[2][1][1]) {
					skor++;
				}
				if(start[3][0][0]==start[3][1][1]) { //�st yanlara bak�yoruz
					skor++;
				}
			}
			if(skor==22) {
				if(start[4][2][0]==k) {//beyaz k��eler e� zamanl� ��z�l�r
					skor++;
				}
				if(start[4][0][0]==k) {//beyaz k��eler e� zamanl� ��z�l�r
					skor++;
				}
				if(start[4][0][2]==k) { //beyaz k��eler e� zamanl� ��z�l�r
					skor++;
				}
				if(start[4][2][2]==k) {//beyaz k��eler e� zamanl� ��z�l�r
					skor++;
				}
			}
			if(skor==26-tolerans) { //�st 3l�ler olduysa yanlara ge�er
				if(start[0][1][0]==start[0][1][1]) { //orta yan
					skor++;
				}
				if(start[0][1][2]==start[0][1][1]) { //orta yan
					skor++;
				}
				if(start[3][1][0]==start[3][1][1]) { //orta yan
					skor++;
				}
				if(start[3][1][2]==start[3][1][1]) { //orta yan
					skor++;
				}
				if(start[2][1][0]==start[2][1][1]) { //orta yan
					skor++;
				}
				if(start[2][1][2]==start[2][1][1]) { //orta yan
					skor++;
				}
				if(start[1][1][0]==start[1][1][1]) { //orta yan
					skor++;
				}
				if(start[1][1][2]==start[1][1][1]) { //orta yan
					skor++;
				}
			}
			if(skor==34-tolerans) {//alt y�zeye �i�ek
				k=start[5][1][1];
				if(start[5][1][0]==k) {
					skor++;
				}
				if(start[5][0][1]==k) {
					skor++;
				}
				if(start[5][1][2]==k) {
					skor++;
				}
				if(start[5][2][1]==k) {
					skor++;
				}
			}
			if(skor==38-tolerans) { // alt y�zey komple
				if(start[5][2][0]==k) {
					skor++;
				}
				if(start[5][0][0]==k) {
					skor++;
				}
				if(start[5][0][2]==k) { 
					skor++;
				}
				if(start[5][2][2]==k) {
					skor++;
				}
			}
			if(skor==42-tolerans) {
				if(start[0][2][0]==start[0][1][1]) { //orta yan
					skor++;
				}
				if(start[0][2][2]==start[0][1][1]) { //orta yan
					skor++;
				}
				if(start[1][2][0]==start[1][1][1]) { //orta yan
					skor++;
				}
				if(start[1][2][2]==start[1][1][1]) { //orta yan
					skor++;
				}
				if(start[2][2][0]==start[2][1][1]) { //orta yan
					skor++;
				}
				if(start[2][2][2]==start[2][1][1]) { //orta yan
					skor++;
				}
				if(start[3][2][0]==start[3][1][1]) { //orta yan
					skor++;
				}
				if(start[3][2][2]==start[3][1][1]) { //orta yan
					skor++;
				}
			}
			if(skor==50-tolerans) {
				if(start[0][2][1]==start[0][1][1]) { 
					skor++;
				}
				if(start[1][2][1]==start[1][1][1]) { 
					skor++;
				}
				if(start[2][2][1]==start[2][1][1]) { 
					skor++;
				}
				if(start[3][2][1]==start[3][1][1]) { 
					skor++;
				}
				
			}
		}
		return skor;
	}
	private static int SkorStandart(List<Integer> val,int[][][] start) {
		//Uyumlu renk say�s�n� d�nd�r�r
		int skor=6; //6 renk zaten yerinde (ortadaki renkler)
		int i;
		for(i=0;i<val.size();i++) {
			start=Slide(start,val.get(i));
		}
		for(i=0;i<6;i++) {
			if(start[i][0][0]==start[i][1][1]) {
				skor++;
			}
			if(start[i][0][1]==start[i][1][1]) {
				skor++;
			}
			if(start[i][0][2]==start[i][1][1]) {
				skor++;
			}
			if(start[i][1][0]==start[i][1][1]) {
				skor++;
			}
			if(start[i][1][2]==start[i][1][1]) {
				skor++;
			}
			if(start[i][2][0]==start[i][1][1]) {
				skor++;
			}
			if(start[i][2][1]==start[i][1][1]) {
				skor++;
			}
			if(start[i][2][2]==start[i][1][1]) {
				skor++;
			}
		}
		return skor;
	}
	private static List<Integer> RandomSolution(boolean randomLen) {
		//Rastgele ��z�m �retir
		List<Integer> val=new ArrayList<>();
		int i;
		Random r = new Random();
		int len=STARTLEN;
		if(randomLen) {
			len=STARTLEN+r.nextInt(16);
		}
		for(i=0;i<len;i++) {
			val.add(r.nextInt(12));
		}
		return val;
	}
	private static void DrawChart(List<Double> list,int[][][] x) { //Grafik �izdirme fonksiyonu
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
        	values.add((double)list.get(i));
		}
        List<Double> scores = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 40;
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add((double) random.nextDouble() * maxScore);
        }
        GraphPanel mainPanel = new GraphPanel(values);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("Do�ru yerle�en renk say�s�");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        LineDrawing l = new LineDrawing(x,"Solution");
        l.setVisible(true);
	}

	private static int[][][] Shuffle(int[][][] startPos){
		//K�p� kar��t�r�r
		int[][][] newPos=new int[6][3][3];
		int i,j,k;
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					newPos[i][j][k]=startPos[i][j][k]; // d�zg�n k�p her y�zeyi ayn� renk
				}
			}
		}
		Random r = new Random();
		int limit=ShuffleCount;
		for(i=0;i<limit;i++) {
			newPos=Slide(newPos,r.nextInt(12));
		}
		return newPos;
	}
	private static void Mutation(double dOrani) { //pathsi etkiliyor
		Random r = new Random(); 

		int move;
		int j;
		int[] positions = new int[(int) (paths.size()*dOrani+1)];
		for (int i = 0; i < paths.size()*dOrani; i++) {
			positions[i]=(r.nextInt(paths.size())); // Mutasyon olacak noktalar belirleniyor
		}
		Arrays.sort(positions);
		j=0;
		for (int i = 0; i < paths.size(); i++) {
			if(j<positions.length) { //Her konumda mutasyon oluyor
				while(positions[j]==i) { // Bir konumda birden fazla mutasyon olma ihtimaline kar��
					move= r.nextInt(12); //Rastgele hamle
					paths.set(i,move);
					j++;
					if(j>=positions.length) {//hepsi tamamlan�nca ��k�l�yor
						break;
					}
				}
			}
		}

	}
	private static List<Integer> CrossOver(List<Integer> val1,List<Integer> val2) {
		List<Integer> result=new ArrayList<>(val1);
		Random r = new Random();
		if(val1.size()>val2.size()) {
			List<Integer> temp = new ArrayList<>(val2);
			val2=new ArrayList<>(val1);
			val1=new ArrayList<>(temp);
			
		}
		int position=r.nextInt(val1.size()-1),length=r.nextInt(val1.size()); //Tek noktadan rastgele uzunlukta crossover

		if(position+length>=val1.size()) {
			length=val1.size()-position;
		}
		for (int i = position; i < position+length; i++) {
			result.set(i, val2.get(i));
		}
		return result;
	}
	private static int[][][] Slide(int[][][] startPos,int move) {
		//D�nd�rme fonksiyonumuz
		int[][][] newPos=new int[6][3][3];
		int i,j,k;
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					newPos[i][j][k]=startPos[i][j][k]; // d�zg�n k�p her y�zeyi ayn� renk
				}
			}
		}
		switch(move) {
		case 0: // saat y�n� 0. y�z
			newPos[0][0][0]=startPos[0][2][0];
			newPos[0][0][1]=startPos[0][1][0];
			newPos[0][0][2]=startPos[0][0][0];
			newPos[0][1][0]=startPos[0][2][1];
			//ortadaki de�i�mez
			newPos[0][1][2]=startPos[0][0][1];
			newPos[0][2][0]=startPos[0][2][2];
			newPos[0][2][1]=startPos[0][1][2];
			newPos[0][2][2]=startPos[0][0][2];

			newPos[1][0][0]=startPos[4][2][0];
			newPos[1][1][0]=startPos[4][2][1];
			newPos[1][2][0]=startPos[4][2][2];

			newPos[5][0][0]=startPos[1][2][0];
			newPos[5][0][1]=startPos[1][1][0];
			newPos[5][0][2]=startPos[1][0][0];
			
			newPos[3][0][2]=startPos[5][0][0];
			newPos[3][1][2]=startPos[5][0][1];
			newPos[3][2][2]=startPos[5][0][2];
			
			newPos[4][2][2]=startPos[3][0][2];
			newPos[4][2][1]=startPos[3][1][2];
			newPos[4][2][0]=startPos[3][2][2];
			break;
		case 1://0 saat y�n� tersi
			newPos[0][0][0]=startPos[0][0][2];
			newPos[0][0][1]=startPos[0][1][2];
			newPos[0][0][2]=startPos[0][2][2];
			newPos[0][1][0]=startPos[0][0][1];
			//ortadaki de�i�mez
			newPos[0][1][2]=startPos[0][2][1];
			newPos[0][2][0]=startPos[0][0][0];
			newPos[0][2][1]=startPos[0][1][0];
			newPos[0][2][2]=startPos[0][2][0];

			newPos[4][2][0]=startPos[1][0][0];
			newPos[4][2][1]=startPos[1][1][0];
			newPos[4][2][2]=startPos[1][2][0];

			newPos[1][2][0]=startPos[5][0][0];
			newPos[1][1][0]=startPos[5][0][1];
			newPos[1][0][0]=startPos[5][0][2];
			
			newPos[5][0][0]=startPos[3][0][2];
			newPos[5][0][1]=startPos[3][1][2];
			newPos[5][0][2]=startPos[3][2][2];
			
			newPos[3][0][2]=startPos[4][2][2];
			newPos[3][1][2]=startPos[4][2][1];
			newPos[3][2][2]=startPos[4][2][0];
			break;
		case 2: //1 saat y�n�
			newPos[1][0][0]=startPos[1][2][0];
			newPos[1][0][1]=startPos[1][1][0];
			newPos[1][0][2]=startPos[1][0][0];
			newPos[1][1][0]=startPos[1][2][1];
			//ortadaki de�i�mez
			newPos[1][1][2]=startPos[1][0][1];
			newPos[1][2][0]=startPos[1][2][2];
			newPos[1][2][1]=startPos[1][1][2];
			newPos[1][2][2]=startPos[1][0][2];

			newPos[2][2][0]=startPos[4][0][2];
			newPos[2][1][0]=startPos[4][1][2];
			newPos[2][0][0]=startPos[4][2][2];

			newPos[5][0][2]=startPos[2][2][0];
			newPos[5][1][2]=startPos[2][1][0];
			newPos[5][2][2]=startPos[2][0][0];
			
			newPos[0][0][2]=startPos[5][0][2];
			newPos[0][1][2]=startPos[5][1][2];
			newPos[0][2][2]=startPos[5][2][2];
			
			newPos[4][0][2]=startPos[0][0][2];
			newPos[4][1][2]=startPos[0][1][2];
			newPos[4][2][2]=startPos[0][2][2];
			break;
		case 3://1 saat y�n� tersi
			newPos[1][0][0]=startPos[1][0][2];
			newPos[1][0][1]=startPos[1][1][2];
			newPos[1][0][2]=startPos[1][2][2];
			newPos[1][1][0]=startPos[1][0][1];
			//ortadaki de�i�mez
			newPos[1][1][2]=startPos[1][2][1];
			newPos[1][2][0]=startPos[1][0][0];
			newPos[1][2][1]=startPos[1][1][0];
			newPos[1][2][2]=startPos[1][2][0];

			newPos[4][0][2]=startPos[2][2][0];
			newPos[4][1][2]=startPos[2][1][0];
			newPos[4][2][2]=startPos[2][0][0];

			newPos[2][2][0]=startPos[5][0][2];
			newPos[2][1][0]=startPos[5][1][2];
			newPos[2][0][0]=startPos[5][2][2];
			
			newPos[5][0][2]=startPos[0][0][2];
			newPos[5][1][2]=startPos[0][1][2];
			newPos[5][2][2]=startPos[0][2][2];
			
			newPos[0][0][2]=startPos[4][0][2];
			newPos[0][1][2]=startPos[4][1][2];
			newPos[0][2][2]=startPos[4][2][2];
			break;

		case 4://2 saat y�n�
			newPos[2][0][0]=startPos[2][2][0];
			newPos[2][0][1]=startPos[2][1][0];
			newPos[2][0][2]=startPos[2][0][0];
			newPos[2][1][0]=startPos[2][2][1];
			//ortadaki de�i�mez
			newPos[2][1][2]=startPos[2][0][1];
			newPos[2][2][0]=startPos[2][2][2];
			newPos[2][2][1]=startPos[2][1][2];
			newPos[2][2][2]=startPos[2][0][2];

			newPos[4][0][0]=startPos[1][0][2];
			newPos[4][0][1]=startPos[1][1][2];
			newPos[4][0][2]=startPos[1][2][2];

			newPos[3][0][0]=startPos[4][0][2];
			newPos[3][1][0]=startPos[4][0][1];
			newPos[3][2][0]=startPos[4][0][0];
			
			newPos[5][2][0]=startPos[3][0][0];
			newPos[5][2][1]=startPos[3][1][0];
			newPos[5][2][2]=startPos[3][2][0];
			
			newPos[1][0][2]=startPos[5][2][0];
			newPos[1][1][2]=startPos[5][2][1];
			newPos[1][2][2]=startPos[5][2][2];
			break;
		case 5://2 ters
			newPos[2][0][0]=startPos[2][0][2];
			newPos[2][0][1]=startPos[2][1][2];
			newPos[2][0][2]=startPos[2][2][2];
			newPos[2][1][0]=startPos[2][0][1];
			//ortadaki de�i�mez
			newPos[2][1][2]=startPos[2][2][1];
			newPos[2][2][0]=startPos[2][0][0];
			newPos[2][2][1]=startPos[2][1][0];
			newPos[2][2][2]=startPos[2][2][0];
			
			newPos[1][0][2]=startPos[4][0][0];
			newPos[1][1][2]=startPos[4][0][1];
			newPos[1][2][2]=startPos[4][0][2];

			newPos[4][0][2]=startPos[3][0][0];
			newPos[4][0][1]=startPos[3][1][0];
			newPos[4][0][0]=startPos[3][2][0];
			
			newPos[3][0][0]=startPos[5][2][0];
			newPos[3][1][0]=startPos[5][2][1];
			newPos[3][2][0]=startPos[5][2][2];
			
			newPos[5][2][0]=startPos[1][0][2];
			newPos[5][2][1]=startPos[1][1][2];
			newPos[5][2][2]=startPos[1][2][2];
			break;
		case 6://3 saat y�n�
			newPos[3][0][0]=startPos[3][2][0];
			newPos[3][0][1]=startPos[3][1][0];
			newPos[3][0][2]=startPos[3][0][0];
			newPos[3][1][0]=startPos[3][2][1];
			//ortadaki de�i�mez
			newPos[3][1][2]=startPos[3][0][1];
			newPos[3][2][0]=startPos[3][2][2];
			newPos[3][2][1]=startPos[3][1][2];
			newPos[3][2][2]=startPos[3][0][2];

			newPos[0][0][0]=startPos[4][0][0];
			newPos[0][1][0]=startPos[4][1][0];
			newPos[0][2][0]=startPos[4][2][0];

			newPos[5][0][0]=startPos[0][0][0];
			newPos[5][1][0]=startPos[0][1][0];
			newPos[5][2][0]=startPos[0][2][0];
			
			newPos[2][0][2]=startPos[5][2][0];
			newPos[2][1][2]=startPos[5][1][0];
			newPos[2][2][2]=startPos[5][0][0];
			
			newPos[4][2][0]=startPos[2][0][2];
			newPos[4][1][0]=startPos[2][1][2];
			newPos[4][0][0]=startPos[2][2][2];
			break;
		case 7://3 ters
			newPos[3][0][0]=startPos[3][0][2];
			newPos[3][0][1]=startPos[3][1][2];
			newPos[3][0][2]=startPos[3][2][2];
			newPos[3][1][0]=startPos[3][0][1];
			//ortadaki de�i�mez
			newPos[3][1][2]=startPos[3][2][1];
			newPos[3][2][0]=startPos[3][0][0];
			newPos[3][2][1]=startPos[3][1][0];
			newPos[3][2][2]=startPos[3][2][0];
			
			newPos[4][0][0]=startPos[0][0][0];
			newPos[4][1][0]=startPos[0][1][0];
			newPos[4][2][0]=startPos[0][2][0];

			newPos[0][0][0]=startPos[5][0][0];
			newPos[0][1][0]=startPos[5][1][0];
			newPos[0][2][0]=startPos[5][2][0];
			
			newPos[5][2][0]=startPos[2][0][2];
			newPos[5][1][0]=startPos[2][1][2];
			newPos[5][0][0]=startPos[2][2][2];
			
			newPos[2][0][2]=startPos[4][2][0];
			newPos[2][1][2]=startPos[4][1][0];
			newPos[2][2][2]=startPos[4][0][0];
			break;
		case 8://4 saat  y�n�
			newPos[4][0][0]=startPos[4][2][0];
			newPos[4][0][1]=startPos[4][1][0];
			newPos[4][0][2]=startPos[4][0][0];
			newPos[4][1][0]=startPos[4][2][1];
			//ortadaki de�i�mez
			newPos[4][1][2]=startPos[4][0][1];
			newPos[4][2][0]=startPos[4][2][2];
			newPos[4][2][1]=startPos[4][1][2];
			newPos[4][2][2]=startPos[4][0][2];
			
			newPos[0][0][0]=startPos[1][0][0];
			newPos[0][0][1]=startPos[1][0][1];
			newPos[0][0][2]=startPos[1][0][2];

			newPos[3][0][0]=startPos[0][0][0];
			newPos[3][0][1]=startPos[0][0][1];
			newPos[3][0][2]=startPos[0][0][2];
			
			newPos[2][0][0]=startPos[3][0][0];
			newPos[2][0][1]=startPos[3][0][1];
			newPos[2][0][2]=startPos[3][0][2];
			
			newPos[1][0][0]=startPos[2][0][0];
			newPos[1][0][1]=startPos[2][0][1];
			newPos[1][0][2]=startPos[2][0][2];
			break;
		case 9://4 saat tersi
			newPos[4][0][0]=startPos[4][0][2];
			newPos[4][0][1]=startPos[4][1][2];
			newPos[4][0][2]=startPos[4][2][2];
			newPos[4][1][0]=startPos[4][0][1];
			//ortadaki de�i�mez
			newPos[4][1][2]=startPos[4][2][1];
			newPos[4][2][0]=startPos[4][0][0];
			newPos[4][2][1]=startPos[4][1][0];
			newPos[4][2][2]=startPos[4][2][0];
			
			newPos[1][0][0]=startPos[0][0][0];
			newPos[1][0][1]=startPos[0][0][1];
			newPos[1][0][2]=startPos[0][0][2];

			newPos[0][0][0]=startPos[3][0][0];
			newPos[0][0][1]=startPos[3][0][1];
			newPos[0][0][2]=startPos[3][0][2];
			
			newPos[3][0][0]=startPos[2][0][0];
			newPos[3][0][1]=startPos[2][0][1];
			newPos[3][0][2]=startPos[2][0][2];
			
			newPos[2][0][0]=startPos[1][0][0];
			newPos[2][0][1]=startPos[1][0][1];
			newPos[2][0][2]=startPos[1][0][2];
			break;
		case 10: //5 saat y�n�
			newPos[5][0][0]=startPos[5][2][0];
			newPos[5][0][1]=startPos[5][1][0];
			newPos[5][0][2]=startPos[5][0][0];
			newPos[5][1][0]=startPos[5][2][1];
			//ortadaki de�i�mez
			newPos[5][1][2]=startPos[5][0][1];
			newPos[5][2][0]=startPos[5][2][2];
			newPos[5][2][1]=startPos[5][1][2];
			newPos[5][2][2]=startPos[5][0][2];
			
			newPos[0][2][0]=startPos[3][2][0];
			newPos[0][2][1]=startPos[3][2][1];
			newPos[0][2][2]=startPos[3][2][2];

			newPos[1][2][0]=startPos[0][2][0];
			newPos[1][2][1]=startPos[0][2][1];
			newPos[1][2][2]=startPos[0][2][2];
			
			newPos[2][2][0]=startPos[1][2][0];
			newPos[2][2][1]=startPos[1][2][1];
			newPos[2][2][2]=startPos[1][2][2];
			
			newPos[3][2][0]=startPos[2][2][0];
			newPos[3][2][1]=startPos[2][2][1];
			newPos[3][2][2]=startPos[2][2][2];
			break;
		case 11:
			newPos[5][0][0]=startPos[5][0][2];
			newPos[5][0][1]=startPos[5][1][2];
			newPos[5][0][2]=startPos[5][2][2];
			newPos[5][1][0]=startPos[5][0][1];
			//ortadaki de�i�mez
			newPos[5][1][2]=startPos[5][2][1];
			newPos[5][2][0]=startPos[5][0][0];
			newPos[5][2][1]=startPos[5][1][0];
			newPos[5][2][2]=startPos[5][2][0];
			
			newPos[3][2][0]=startPos[0][2][0];
			newPos[3][2][1]=startPos[0][2][1];
			newPos[3][2][2]=startPos[0][2][2];

			newPos[0][2][0]=startPos[1][2][0];
			newPos[0][2][1]=startPos[1][2][1];
			newPos[0][2][2]=startPos[1][2][2];
			
			newPos[1][2][0]=startPos[2][2][0];
			newPos[1][2][1]=startPos[2][2][1];
			newPos[1][2][2]=startPos[2][2][2];
			
			newPos[2][2][0]=startPos[3][2][0];
			newPos[2][2][1]=startPos[3][2][1];
			newPos[2][2][2]=startPos[3][2][2];
			break;
		}
		return newPos;
	}

}