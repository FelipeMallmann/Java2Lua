a = 10
b = 20
function printaSoma(a, b)    
    local r = a + b
    print(r)
    
    if (r >= 0) then        
        
        if (r >= 10) then            
            print("Resultado maior ou igual a 10.")
                    
        else            
            print("Resultado menor que 10.")
                    
        end
            
    else        
        print("Resultado menor que 0.")
            
    end
    
    local i = 0
    while (i < a) do        
        
        local j = 0
        while (j < i) do            
            print(j)
            j = j + 1        
        end
        i = i + 1    
    end
    
end

function main(args)    
    printaSoma(a, b)
    local a = 3
    local b = 5
    print("\n-------------\n")
    printaSoma(a, b)
    
end

-- Function Starter
main()