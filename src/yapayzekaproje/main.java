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
	static float angleSkor=0; // Açý deðerleri toplamý
	static int STARTLEN=4; // Baþlangýç dizi boyutumuz
	static int ShuffleCount=50; //Küpü kaç kere karýþtýracaðýmýz
	static int tolerans=3; //Teori deðerlendirme fonk. için tolerans deðeri

	static int maxDenemeSayisi=3000; //maksimum iterasyon sayýsý
	static int P=200;
	static double degisimOrani; //mutasyon oraný
	static int[][][] cubeStart; 
	
	
	static List<Integer> paths; // Þuanki deðerler dizisi
	static List<Integer> solution; // En iyi deðer
	static double bestSkor;
	static double yerelMaxSkor=0;

	static List<Integer> yerelMaxSolution; //Yerel maksimum deðeri
	static List<Double> chartValues; //Grafik yazdýrmak için deðerleri tutar
	static double cur_skor; //Mutasyonda önceki deðerle karþýlaþtýrmak için deðerler
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
					cubeStart[i][j][k]=i; // düzgün küp her yüzeyi ayný renk
				}
			}
		}

		List<List<Integer>> populasyon= new ArrayList<>();
		List<List<Integer>> populasyonNew= new ArrayList<>();
		
		int nesilSayisi=16,populasyonSayisi=10; //Nesil ve popülasyon deðerleri
		cubeStart=Shuffle(cubeStart);
		cubeBase=cubeStart.clone();
		for (k = 0; k < populasyonSayisi; k++) { //Rastgele baþlangýç yaratýlýyor
			List<Integer> temp = new ArrayList<>();
			temp=new ArrayList<>(RandomSolution(false));
			populasyon.add(new ArrayList<>(temp));
			paths=populasyon.get(k);

		}

		PrintCube(cubeStart);
		solution=new ArrayList<>(paths);
		yerelMaxSolution=new ArrayList<>(paths);
		
        LineDrawing ld = new LineDrawing(cubeStart,"Start"); //Baþlangýç küpünü çizdiriyoruz
        ld.setVisible(true);
        
		for (int nesil = 1; nesil < nesilSayisi; nesil++) {
			for (k = 0; k < populasyonSayisi; k++) {
				//Popülasyonun her bir bireyine mutasyonlar uygulanýr
				paths=new ArrayList<>(populasyon.get(k));
				bestSkor=Skor(paths,cubeStart);
				solution=new ArrayList<>(paths);

				yerelMaxSkor=bestSkor;
				yerelMaxSolution=new ArrayList<>(paths);

				i=0;
				int z=0;
				while ( z <maxDenemeSayisi) {
					i=0;
					degisimOrani=0.16f; // mutasyon baþlangýç oraný

					while (i<P) {
						z++;
						paths=new ArrayList<>(yerelMaxSolution);
						counter++;
						Mutation(degisimOrani); //Mutasyon oranýnda paths'e mutasyon uygulanýr
						cur_skor=Skor(paths,cubeStart);
						if(cur_skor>yerelMaxSkor) {
							yerelMaxSkor=cur_skor;
							yerelMaxSolution=new ArrayList<>(paths);
						}
						if(cur_skor>prev_skor) {
							degisimOrani*=0.98f; //Kötüleþtiyse deðer mutasyon artar
						}else {
							degisimOrani*=1.02f;
							if(degisimOrani>0.8) { // Mutasyon 0.8 i geçemez
								degisimOrani=0.8;
								//System.out.println("GEÇTÝ!!!!!!!!!!");
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
				populasyonNew.add(solution); // Oluþan en iyi durumu birey olarak ekliyoruz
			}
			populasyon.clear();

			float best=Skor(populasyonNew.get(0),cubeStart); // en iyi bireyin indeksini buluyoruz
			int bestIndex=0;
			for (int l = 0; l < populasyonNew.size(); l++) {
				if(Skor(populasyonNew.get(l),cubeStart)>best) {
					bestIndex=l;
				}

			}
			chartValues.add((double)Skor(populasyonNew.get(bestIndex),cubeStart)); //Neslin en iyi bireyine grafiðe ekliyoruz

			populasyon.add(populasyonNew.get(bestIndex)); //En iyi bireyi gelecek nesle 2 kez ekliyoruz
			for (int l = 1; l < populasyonNew.size(); l++) {

				if(l<populasyonNew.size()/4) { //Rastgele bireyler ekliyoruz
					populasyon.add(populasyonNew.get(r.nextInt(populasyonSayisi)));
				}else {
					//Yarýsý da crossoverdan geliyor
					populasyon.add(CrossOver(populasyonNew.get(r.nextInt(populasyonNew.size())), populasyonNew.get(r.nextInt(populasyonNew.size()))));
				}
			}
			System.out.print("Seçilen birey: Nesil "+nesil+" Birey "+bestIndex+"/"+populasyon.size()+" : "+Skor(populasyonNew.get(bestIndex),cubeStart)+"/54 \n");
			System.out.println(populasyonNew.get(bestIndex));

			if(bestIndex>=populasyonNew.size()/4) {
				System.out.println("Cross Overdan birey elde edildi");
			}
			solution= new ArrayList<>(populasyonNew.get(bestIndex));
			
			//Önceki pathi kaydedip þimdiki haline yeni çözüm dizisi arýyoruz
			
			if(Skor(populasyonNew.get(bestIndex),cubeStart)>process) {
				process=Skor(populasyonNew.get(bestIndex),cubeStart);
				for(i=0;i<solution.size();i++) {
					cubeStart=Slide(cubeStart,solution.get(i));
					totalSolution.add(solution.get(i));
				}
				solution.clear();
				for(int l=0;l<STARTLEN*3;l++) {//sabit duran bir çözüm ekleyelim 
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
		if(skor==10) { // Çiçek þekli oluþtuysa
			if(start[0][0][1]==start[0][1][1]) { //orta üst ve orta eþitse
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
				if(start[1][0][0]==start[1][1][1]) { //üst yanlara bakýyoruz
					skor++;
				}
				if(start[0][0][0]==start[0][1][1]) { //üst yanlara bakýyoruz
					skor++;
				}
				if(start[3][0][2]==start[3][1][1]) {
					skor++;
				}

				if(start[1][0][2]==start[1][1][1]) {
					skor++;
				}
				if(start[2][0][0]==start[2][1][1]) { //üst yanlara bakýyoruz
					skor++;
				}
				if(start[2][0][2]==start[2][1][1]) {
					skor++;
				}
				if(start[3][0][0]==start[3][1][1]) { //üst yanlara bakýyoruz
					skor++;
				}
			}
			if(skor==22) {
				if(start[4][2][0]==k) {//beyaz köþeler eþ zamanlý çözülür
					skor++;
				}
				if(start[4][0][0]==k) {//beyaz köþeler eþ zamanlý çözülür
					skor++;
				}
				if(start[4][0][2]==k) { //beyaz köþeler eþ zamanlý çözülür
					skor++;
				}
				if(start[4][2][2]==k) {//beyaz köþeler eþ zamanlý çözülür
					skor++;
				}
			}
			if(skor==26-tolerans) { //Üst 3lüler olduysa yanlara geçer
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
			if(skor==34-tolerans) {//alt yüzeye çiçek
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
			if(skor==38-tolerans) { // alt yüzey komple
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
		//Uyumlu renk sayýsýný döndürür
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
		//Rastgele çözüm üretir
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
	private static void DrawChart(List<Double> list,int[][][] x) { //Grafik çizdirme fonksiyonu
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
        JFrame frame = new JFrame("Doðru yerleþen renk sayýsý");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        LineDrawing l = new LineDrawing(x,"Solution");
        l.setVisible(true);
	}

	private static int[][][] Shuffle(int[][][] startPos){
		//Küpü karýþtýrýr
		int[][][] newPos=new int[6][3][3];
		int i,j,k;
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					newPos[i][j][k]=startPos[i][j][k]; // düzgün küp her yüzeyi ayný renk
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
				while(positions[j]==i) { // Bir konumda birden fazla mutasyon olma ihtimaline karþý
					move= r.nextInt(12); //Rastgele hamle
					paths.set(i,move);
					j++;
					if(j>=positions.length) {//hepsi tamamlanýnca çýkýlýyor
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
		//Döndürme fonksiyonumuz
		int[][][] newPos=new int[6][3][3];
		int i,j,k;
		for(i=0;i<6;i++) {
			for(j=0;j<3;j++) {
				for(k=0;k<3;k++) {
					newPos[i][j][k]=startPos[i][j][k]; // düzgün küp her yüzeyi ayný renk
				}
			}
		}
		switch(move) {
		case 0: // saat yönü 0. yüz
			newPos[0][0][0]=startPos[0][2][0];
			newPos[0][0][1]=startPos[0][1][0];
			newPos[0][0][2]=startPos[0][0][0];
			newPos[0][1][0]=startPos[0][2][1];
			//ortadaki deðiþmez
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
		case 1://0 saat yönü tersi
			newPos[0][0][0]=startPos[0][0][2];
			newPos[0][0][1]=startPos[0][1][2];
			newPos[0][0][2]=startPos[0][2][2];
			newPos[0][1][0]=startPos[0][0][1];
			//ortadaki deðiþmez
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
		case 2: //1 saat yönü
			newPos[1][0][0]=startPos[1][2][0];
			newPos[1][0][1]=startPos[1][1][0];
			newPos[1][0][2]=startPos[1][0][0];
			newPos[1][1][0]=startPos[1][2][1];
			//ortadaki deðiþmez
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
		case 3://1 saat yönü tersi
			newPos[1][0][0]=startPos[1][0][2];
			newPos[1][0][1]=startPos[1][1][2];
			newPos[1][0][2]=startPos[1][2][2];
			newPos[1][1][0]=startPos[1][0][1];
			//ortadaki deðiþmez
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

		case 4://2 saat yönü
			newPos[2][0][0]=startPos[2][2][0];
			newPos[2][0][1]=startPos[2][1][0];
			newPos[2][0][2]=startPos[2][0][0];
			newPos[2][1][0]=startPos[2][2][1];
			//ortadaki deðiþmez
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
			//ortadaki deðiþmez
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
		case 6://3 saat yönü
			newPos[3][0][0]=startPos[3][2][0];
			newPos[3][0][1]=startPos[3][1][0];
			newPos[3][0][2]=startPos[3][0][0];
			newPos[3][1][0]=startPos[3][2][1];
			//ortadaki deðiþmez
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
			//ortadaki deðiþmez
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
		case 8://4 saat  yönü
			newPos[4][0][0]=startPos[4][2][0];
			newPos[4][0][1]=startPos[4][1][0];
			newPos[4][0][2]=startPos[4][0][0];
			newPos[4][1][0]=startPos[4][2][1];
			//ortadaki deðiþmez
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
			//ortadaki deðiþmez
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
		case 10: //5 saat yönü
			newPos[5][0][0]=startPos[5][2][0];
			newPos[5][0][1]=startPos[5][1][0];
			newPos[5][0][2]=startPos[5][0][0];
			newPos[5][1][0]=startPos[5][2][1];
			//ortadaki deðiþmez
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
			//ortadaki deðiþmez
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