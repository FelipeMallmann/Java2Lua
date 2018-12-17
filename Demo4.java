class Demo4 {

    public boolean acabou = false;
    public int b = 25;

    public int teste(int a){
        int numeroDePares = 0;

        if (a > 0){
            int i = 0;
            while (i < a){
                System.out.println(i);
                i++;

                if (i % 2 == 0)
                    numeroDePares++;
            }
        }
        return numeroDePares;
    }

    public static void main(String[] args){
        Demo4 main = new Demo4();
        System.out.println("\nPares:" + main.teste(main.b));
    }
}