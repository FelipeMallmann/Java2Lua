acabou = false
b = 25
function teste(a)    
    local numeroDePares = 0
    
    if (a > 0) then        
        local i = 0
        
        while (i < a) do            
            print(i)
            i = i + 1
            if (i % 2 == 0) then                
                numeroDePares = numeroDePares + 1            
            end
                    
        end
            
    end
    return numeroDePares 
end

function main(args)    
    print("\nPares:" .. teste(b))
    
end

-- Function Starter
main()