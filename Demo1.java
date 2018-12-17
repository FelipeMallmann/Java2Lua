class Demo1 {

  public String printaNumerosIntervalo(int nInicial, int nFinal){
      if(nInicial > nFinal){
        return "Inicial deve ser menor que final";
      };
      
      for (int i = nInicial; i <= nFinal; i++){
        System.out.println(i);
      };
    return "";
  }

  public double resultadoSoma(double a, double b){
    double result = (a + b);
    return result;
  }
		
  public String sexoPessoa(char s){
    String result;
    
    if (s == 'M'){
      result = "Masculino";
    } else if (s == 'F'){
      result = "Feminino";
    }else{
      result = "ERRO";
    };
    
    return "Sexo da pessoa: " + result;
  }

  public static void main(String[] args){
    Demo1 teste = new Demo1();

		double x, y;
		
		x = 125.00;
		y = 88.00;
		
		System.out.println("Resultado: " + teste.resultadoSoma(x, y));	
	
		System.out.println(teste.sexoPessoa('F'));
		System.out.println(teste.sexoPessoa('x'));
		
		teste.printaNumerosIntervalo(5, 75);
  }

}