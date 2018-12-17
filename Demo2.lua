function main(args)    
  local index = 0
  local alphabet = { 'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' , 'I' , 'J' , 'K' , 'L' , 'M' , 'N' , 'O' , 'P' , 'Q' , 'R' , 'S' , 'T' , 'U' , 'V' , 'W' , 'X' , 'Y' , 'Z' }
  print("Alphabet:")
  
  while (index < 26) do        
      print(alphabet[index])
      index = index + 1    
  end
  print("End.")
  
end

-- Function Starter
main()