import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;

public class Java2Lua extends JavaBaseListener {

    // Parser
    JavaParser parser;

    // Vocabulary
    Vocabulary vocabulary = JavaLexer.VOCABULARY;

    // Tokens
    TokenStream tokens = null;

    // Constructor
    public Java2Lua(JavaParser parser) {
        this.parser = parser;
    }

    // Context
    LinkedList<String> context = new LinkedList<String>();

    boolean inFieldDeclaration = false;
    boolean inVariableDeclaration = false;
    boolean inFormalParamList = false;
    boolean inAssignment = false;

    boolean inIfDeclaration = false;
    boolean inWhileDeclaration = false;

    boolean inForDeclaration = false;
    boolean inForInitDeclaration = false;
    boolean inForExpressionDeclaration = false;
    boolean inForUpdateDeclaration = false;

    String className = "";
    String varToIgnore = "";

    String forInit = "";
    LinkedList<String> forUpdate = new LinkedList<String>();
    String forExpression = "";

    FileWriter fileWriter = null;
    
    // Saver
    public void printa(String s){
        System.out.print(s);
        try { fileWriter.write(s); }
        catch (Exception e) { e.printStackTrace(); }
    }

    // Tabulation
    String tab = "";
    public void setTab(int x){
        if (x < 0)
            tab = tab.substring(0, tab.length() + (x*4));
        else
            for (int i = 0; i < x; i++)
                tab = tab + "    ";
        printa(tab);
    }
    public void newLine(){
        printa("\n" + tab);
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // COMPILATION UNIT -> Start / Exit
    @Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        tokens = parser.getTokenStream();

        try { fileWriter = new FileWriter("converted.lua"); } 
        catch (Exception e) { e.printStackTrace(); }
    }
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        printa("-- Function Starter\n");
        printa("main()\n");
        try { fileWriter.close(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLASS DECLARATION -> className
    @Override
    public void enterNormalClassDeclaration(JavaParser.NormalClassDeclarationContext ctx) {      
        int start = ctx.getStart().getTokenIndex();
	    int stop = ctx.getStop().getTokenIndex();

        for (int i = start; i <= stop; i++){
            String tokenName = tokens.get(i).getText();
            String displayName = vocabulary.getDisplayName(tokens.get(i).getType());

            if (displayName == "Identifier" && !tokenName.equals("String")){
                className = tokenName;
                break;
            }
        }               
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // FIELD DECLARATION LOGIC
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {      
        inFieldDeclaration = true;      
    }
    @Override
    public void exitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {      
        inFieldDeclaration = false;       
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // METHODS DECLARATION LOGIC
	@Override
    public void enterMethodDeclarator(JavaParser.MethodDeclaratorContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();

        printa("function ");
        for (int i = start; i <= stop; i++){
            String tokenName = tokens.get(i).getText();

            printa(tokenName);
            if (tokenName.equals("("))
                break;
        }
        
    }
    @Override
    public void exitMethodDeclarator(JavaParser.MethodDeclaratorContext ctx) {
        printa(")");
    }
    @Override
    public void enterMethodBody(JavaParser.MethodBodyContext ctx) {
        setTab(1);
        newLine();    
    }
    @Override
    public void exitMethodBody(JavaParser.MethodBodyContext ctx) {
        setTab(-1);
        newLine();
        printa("end");
        newLine(); 
        newLine();
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // PARAMETERS LOGIC
	@Override
    public void enterFormalParameterList(JavaParser.FormalParameterListContext ctx) {
        inFormalParamList = true;

        int start = ctx.getStart().getTokenIndex();
	    int stop = ctx.getStop().getTokenIndex();

        List<String> params = new ArrayList<>();
        for (int i = start; i <= stop; i++){
            String tokenName = tokens.get(i).getText();
            String displayName = vocabulary.getDisplayName(tokens.get(i).getType());

            if (displayName == "Identifier" && !tokenName.equals("String"))
                params.add(tokens.get(i).getText());           
        }
        for (int i = 0; i < params.size(); i++){
            if ( params.size() == 1 || i == ( params.size() - 1 ) )
                printa(params.get(i));
            else
                printa(params.get(i) + ", ");            
        }
        params = new ArrayList<>();
    }
    @Override
    public void exitFormalParameterList(JavaParser.FormalParameterListContext ctx) {
        inFormalParamList = false;
    }
    
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // VARIABLE LOGIC
    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();

        boolean shouldStop = false;
        String variableName = "";

        if(!inForDeclaration){
            for (int i = start; i <= stop; i++){
                String tokenName = tokens.get(i).getText();

                // Ignore Main constructor variable
                if (tokenName.equals("new") && tokens.get(i + 1).getText().equals(className)){
                    varToIgnore = tokens.get(i - 2).getText();
                    shouldStop = true;
                    break;
                }
                else
                    variableName = variableName + tokenName;      
                if (! (stop == start + 1 || i == stop) )
                        variableName = variableName + " ";

            }
            if (!shouldStop){
                if (!inFieldDeclaration)
                    printa("local " + variableName);      
                else  
                    printa(variableName);
                newLine();
            }
        }
    }

    @Override
    public void enterAssignment(JavaParser.AssignmentContext ctx) {
        inAssignment = true;
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();

        for (int i = start; i <= stop; i++){
            String tokenName = tokens.get(i).getText();
            tokenName = FixTokenName(tokenName);

            printa(tokenName + " ");     
        }
        newLine();
    }
    @Override
    public void exitAssignment(JavaParser.AssignmentContext ctx) {
        inAssignment = false;
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // EXPRESSION LOGIC
    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();

        if (inIfDeclaration || inWhileDeclaration){
            for (int i = start; i <= stop; i++){
                String tokenName = tokens.get(i).getText();
                tokenName = FixTokenName(tokenName);

                printa(tokenName);           
                if (! (stop == start + 1 || i == stop) )
                    printa(" ");
            }

            if (inIfDeclaration)
                inIfDeclaration = false;
            else if (inWhileDeclaration)
                inWhileDeclaration = false;
        }

        if (inForExpressionDeclaration){
            for (int i = start; i <= stop; i++){
                String tokenName = tokens.get(i).getText();
                tokenName = FixTokenName(tokenName);

                forExpression = forExpression + tokenName;
                if (! (stop == start + 1 || i == stop) )
                    forExpression = forExpression + " ";
            }
            inForExpressionDeclaration = false;
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // RETURN LOGIC
    @Override
    public void enterReturnStatement(JavaParser.ReturnStatementContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();

        for (int i = start; i <= stop; i++){
            String tokenName = tokens.get(i).getText();

            if (tokenName.equals("+"))
                tokenName = " .. ";

            if (tokenName.equals(";"))
                break;

            printa(tokenName + " ");
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // IF LOGIC
    @Override
    public void enterIfThenStatement(JavaParser.IfThenStatementContext ctx) {
        context.add("ifthen");
        inIfDeclaration = true;

        newLine();
        printa("if (");
    }  
    @Override
    public void enterIfThenElseStatement(JavaParser.IfThenElseStatementContext ctx) {
        context.add("ifthenelse");
        inIfDeclaration = true;

        newLine();
        printa("if (");
    }
    @Override
    public void exitIfThenStatement(JavaParser.IfThenStatementContext ctx) {
        setTab(-1);
        newLine();
        printa("end");
        newLine();
    }  
    @Override
    public void exitIfThenElseStatement(JavaParser.IfThenElseStatementContext ctx) {
        setTab(-1);
        newLine();
        printa("end");
        newLine();
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // WHILE LOGIC
    @Override
    public void enterWhileStatement(JavaParser.WhileStatementContext ctx) {
        context.add("while");
        inWhileDeclaration = true;

        newLine();
        printa("while (");
    }
    @Override
    public void exitWhileStatement(JavaParser.WhileStatementContext ctx) {
        //context.removeLast();
        setTab(-1);
        newLine();
        printa("end");
        newLine();
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // FOR LOGIC
    @Override
    public void enterBasicForStatement(JavaParser.BasicForStatementContext ctx){
        inForDeclaration = true;
    }
    @Override
    public void enterForInit(JavaParser.ForInitContext ctx) {
        inForInitDeclaration = true;
        int start = ctx.getStart().getTokenIndex() + 1;
        int stop = ctx.getStop().getTokenIndex();

        for (int i = start; i <= stop; i++){
            String tokenName = tokens.get(i).getText();
            forInit = forInit + tokenName;

            if (! (stop == start + 1 || i == stop) )
                    forInit = forInit + " ";
        }
        forInit = "local " + forInit;
    }
    @Override
    public void exitForInit(JavaParser.ForInitContext ctx) {
        inForInitDeclaration = false;
        inForExpressionDeclaration = true;
    }
    @Override
    public void enterForUpdate(JavaParser.ForUpdateContext ctx) {
        inForUpdateDeclaration = true;
    }
    @Override
    public void exitForUpdate(JavaParser.ForUpdateContext ctx) {
        inForDeclaration = false;
        newLine();
        printa(forInit); forInit = "";
        newLine();
        printa("while (");
        printa(forExpression); forExpression = "";
        printa(") do");
        setTab(1);
        newLine();
    }
    @Override
    public void exitBasicForStatement(JavaParser.BasicForStatementContext ctx) {
        String forUpd = forUpdate.getLast();
        forUpdate.removeLast();

        printa(forUpd);
        setTab(-1);
        newLine();
        printa("end");
        newLine();
    }
    

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // STATEMENT LOGIC
    @Override
    public void enterStatement(JavaParser.StatementContext ctx) {
        if (context.size() > 0){
            String currentContext = context.getLast();

            if (currentContext.equals("ifthen")){
                context.removeLast();
                printa(") then");
                setTab(1);
                newLine();
            }    
            else if (currentContext.equals("while")){
                context.removeLast();
                printa(") do");
                setTab(1);
                newLine();
            }            
        }
    }
    @Override
    public void enterStatementNoShortIf(JavaParser.StatementNoShortIfContext ctx) {
        if (context.size() > 0){
            String currentContext = context.getLast();

            if (currentContext.equals("ifthenelse")){
                
                printa(") then");
                setTab(1);
                newLine();
            }
        }
    }
    @Override
    public void exitStatementNoShortIf(JavaParser.StatementNoShortIfContext ctx) { 
        if (context.size() > 0){
            String currentContext = context.getLast();

            if (currentContext.equals("ifthenelse")){
                context.removeLast();
                setTab(-1);
                newLine();
                printa("else");
                setTab(1);
                newLine();
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // METHOD INVOCATION LOGIC
    @Override
    public void enterMethodInvocation(JavaParser.MethodInvocationContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();
    
        String methodName = "";
        for (int i = start; i < stop; i++){
            String tokenName = tokens.get(i).getText();

            if (tokenName.equals("(")){
                start = i;
                break;
            }

            if (! (tokenName.equals(varToIgnore) && tokens.get(i+1).getText().equals(".")) ){
                methodName = methodName + tokenName;
            }else{
                i++;
            }
        }
        switch (methodName){
            case "System.out.println":  
                //save ("print(\"\\n\" .. ")
                printa("print(");
                break;

            case "System.out.print":    
                printa("print(");
                break;

            default:
                printa(methodName + "(");
                break;
        }

        // Argument List
        for (int i = start + 1; i < stop; i++){
            String tokenName = tokens.get(i).getText();
            if (tokenName.equals(","))
                printa(tokenName + " ");
            
            else if (tokenName.equals("+"))
                printa(" " + ".." + " ");
            
            else
                if (! (tokenName.equals(varToIgnore) || tokenName.equals(".")) )
                    printa(tokenName);           
        }
	}
    @Override
    public void exitMethodInvocation(JavaParser.MethodInvocationContext ctx) {
        printa(")");
        newLine();        
	}

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // INCREMENT/DECREMENT LOGIC
    @Override
    public void enterPostIncrementExpression(JavaParser.PostIncrementExpressionContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        String tokenName = tokens.get(start).getText();

        if (inForUpdateDeclaration)
            forUpdate.add(tokenName + " = " + tokenName + " + 1"); 
        else
            printa(tokenName + " = " + tokenName + " + 1");
    }
    public void enterPostDecrementExpression(JavaParser.PostDecrementExpressionContext ctx) {
        int start = ctx.getStart().getTokenIndex();
        String tokenName = tokens.get(start).getText();

        if (inForUpdateDeclaration)
            forUpdate.add(tokenName + " = " + tokenName + " - 1");
        else
            printa(tokenName + " = " + tokenName + " - 1");
    }
    
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // <--> LOGIC
    public String FixTokenName(String n){
        if (n.equals("!"))
            return "not";

        else if (n.equals("&&"))
            return "and";

        else if (n.equals("||"))
            return "or";

        else if (n.equals("null"))
            return "nil";

        return n;
    }
}