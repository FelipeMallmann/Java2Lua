class Demo2 {

  public static void main(String[] args){
    int index = 0;
    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 
    'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    
    System.out.println("Alphabet:");

    while(index < 26){
      System.out.println(alphabet[index]);
      index++;
    }

    System.out.println("End.");
  }

}