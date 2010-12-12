#include "../gcd-body/gcdBody.h"

int main(const int argc,const char* argv[])
{
    // execute body
    std::string intermediateFilePath,outputFilePath;
    int result = body(intermediateFilePath,outputFilePath,argc,argv,
                      ".clu.i",".clu.cpp.g","de.gaalop.compressed.Plugin",".cu.i",".o","-o");
    if(result)
        return result;

    // read settings
#ifdef WIN32
    std::string compilerPath("../share/gcd/cuda_settings.bat");
#else
    std::string compilerPath("../share/gcd/cuda_settings.sh");
#endif

    // invoke compiler
    invokeCompiler(compilerPath,argc,argv,
                   outputFilePath,intermediateFilePath,"-o");

    return result;
}
