import java.awt.Color;
import java.util.Random;


public class DNA {
	private static final int LIFE_DEV = 30, LIFE_MEAN = 500, LIFE_MOD = 2,
	FEMALE = 0, MALE = 1, DISEASE_DEATH_TIME = 6, MAX_HUNGER = 50;
	private static final double MUTATION_PROBABILITY = 0.0005, DISEASE_VIRULENCE = 0.4;
	
	
	//Eye Color
	//Blue FF, Green TF, MAGENTA TT
	boolean eye1, eye2;
	int fertility;
	
	//Lifespan
	//Use the better one of the two you have
	short life1, life2;
	int lifeSpan;
	
	//Hunger + strength
		
	
	//disease resistance
	boolean disease1, disease2;
	
	//anger
	short anger1, anger2;
	double anger;
	
	//gender
	private int gender;
	
	
	public static final Random r = new Random();
	
	public DNA(){
		gender = r.nextBoolean()  ? 1 : 0 ; 
		eye1 = r.nextBoolean();
		eye2 = r.nextBoolean();
		life1 = (short)r.nextInt(Short.MAX_VALUE);
		life2 = (short)r.nextInt(Short.MAX_VALUE);
		anger1 = (short)r.nextInt(Short.MAX_VALUE);
		anger2 = (short)r.nextInt(Short.MAX_VALUE);
		disease1 = r.nextBoolean();
		disease2 = r.nextBoolean();
		
		
		short l1 = 0,l2=0, a1 =0, a2 = 0;
		int mod = 0x1;
		for(int i = 0 ; i < 16; i++){
			l1 += (life1 & mod) == 0 ? 0 : 1;
			l2 += (life2 & mod) == 0 ? 0 : 1;
			a1 += (anger2 & mod) == 0 ? 0 : 1;
			a2 += (anger2 & mod) == 0 ? 0 : 1;
			mod = mod << 1;
		}
		int l = Math.max(l1,l2) - 8;
				
		lifeSpan = (int)(r.nextGaussian()*LIFE_DEV + (LIFE_MEAN +  (l * LIFE_MOD)));
		anger = (a1 + a2) / 100.0;
		computeFertility();
		
	}
	
	public DNA(DNA d1, DNA d2){
		gender = r.nextBoolean()  ? 1 : 0 ; 
		eye1 = d1.inheritEyeGene();
		eye2 = d2.inheritEyeGene();
		life1 = d1.inheritLife();
		life2 = d2.inheritLife();
		disease1 = d1.inheritDisease();
		disease2 = d2.inheritDisease();
		anger1 = d1.inheritAnger();
		anger2 = d2.inheritAnger();
		
		short l1 = 0,l2=0, a1 =0, a2 = 0;
		int mod = 0x1;
		for(int i = 0 ; i < 16; i++){
			l1 += (life1 & mod) == 0 ? 0 : 1;
			l2 += (life2 & mod) == 0 ? 0 : 1;
			a1 += (anger2 & mod) == 0 ? 0 : 1;
			a2 += (anger2 & mod) == 0 ? 0 : 1;
			mod = mod << 1;
		}
		int l = Math.max(l1,l2) - 8;
		
		lifeSpan = (int)(r.nextGaussian()*LIFE_DEV + (LIFE_MEAN +  (l * LIFE_MOD)));
		anger = (a1 + a2) / 100.0;
		computeFertility();
	}
	
	private void computeFertility(){
		if((eye1 && eye2) || (!eye1 && !eye2)){
			fertility = 50; 
		}else{
			fertility = 40;
		}
		if(this.gender == MALE){
			this.fertility /=2;
		}
		this.fertility /=2;
	}
	
	public int getLifespan(){
		
		return lifeSpan;
	}
	
	public double getAnger(){
		return anger;
	}
	
	public int getFertility(){
		return fertility;
	}
	
	public Color getEyeColor(){
		Color c;
		if (!(eye1 || eye2)) {
			c = Color.blue;
		} else if (eye1 && eye2) {
			c = Color.MAGENTA;
		} else if (eye1 || eye2) {
			c = Color.green;
		} else {
			c = Color.pink;
		}
		if (gender == FEMALE) {
			return c.brighter();
		}
		return c.darker();
	}
	
	private boolean inheritEyeGene(){
		if(r.nextBoolean()){
			return eye1;
		}
		return eye2;
	}
	
	private boolean inheritDisease(){
		if(r.nextBoolean()){
			return disease1;
		}
		return disease2;
	}
	
	private short inheritLife(){
		if(r.nextBoolean()){
			return life1;
		}
		return life2;
	}
	
	private short inheritAnger(){
		short a = anger2;
		if(r.nextBoolean()){
			a= anger1;
		}
		
		if( r.nextDouble() < MUTATION_PROBABILITY){
			short v = (short) r.nextInt(Short.MAX_VALUE);
			switch(r.nextInt(3)){
			case 0:
				a |= v;
				break;
			case 1:
				a &= v;
				break;
			case 2:
				a ^= v;
				break;
			}
		}
		return a;
	}
	
	public int eyeRating(){
		if (!(eye1 || eye2)) {
			return 0;
		} else if (eye1 && eye2) {
			return 2;
		} else if (eye1 || eye2) {
			return 1;
		} else{
			return 7;
		}
	}
	
	public int getGender(){
		return gender;
	}
	
	public int diseaseDeathTime(){
		int time = 0;
		if(disease1){
			 time +=1;
		}
		if(disease2){
			time +=1;
		}
		return DISEASE_DEATH_TIME * (time +1);
	}
	
	public double diseaseVirulence(){
		int time = 0;
		if(disease1){
			 time ++;
		}
		if(disease2){
			time ++;
		}
		return DISEASE_VIRULENCE * (time +1);
	}
	
	public boolean isImmune(){
		 return (disease1 && ! disease2) && gender == FEMALE ;
	}
	
	
	public int maxHunger(){
		return MAX_HUNGER;
	}
	
	public int getStrength(){
		if (!(eye1 || eye2)) {
			return 30;
		} else if (eye1 && eye2) {
			return 30;
		} else if (eye1 || eye2) {
			return 50;
		} else{
			return 7;
		}
	}
	
}
