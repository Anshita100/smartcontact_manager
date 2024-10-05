console.log("this is script file");

const toggleSidebar = () =>{

    if($(".sidebar").is(":visible"))
    {
        //true
        //sidebar close karo
        $(".sidebar").css("display","none");
        
        $(".content").css("margin-left","0%");
        
    }
    else
    {
        //false
        //sidebar open karo
        $(".sidebar").css("display","block");
        
        $(".content").css("margin-left","20%");
        
    }
};