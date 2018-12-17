class Demo3 {

    public int a = 10;
    public int b = 20;

    public void printaSoma(int a, int b){
        int r = a + b;
        System.out.println(r);
        
        if (r >= 0){
            if (r >= 10)
                System.out.println("Resultado maior ou igual a 10.");
            else
                System.out.println("Resultado menor que 10.");
        }
        else
            System.out.println("Resultado menor que 0.");   

        for (int i = 0; i < a; i++)
            for (int j = 0; j < i; j++)
                System.out.println(j);     
    }

    public static void main(String[] args){
        Demo3 teste = new Demo3();
        teste.printaSoma(teste.a, teste.b);

        int a = 3;
        int b = 5;
        System.out.print("\n-------------\n");
        teste.printaSoma(a,b);
    }
}