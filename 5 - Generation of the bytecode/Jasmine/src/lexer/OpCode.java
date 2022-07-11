package lexer;

// Enumerazione dei nomi mnemonici relativi ai comandi del linguaggio target
// fa riferimento alle istruzioni della JVM

public enum OpCode {
    ldc, imul, ineg, idiv, iadd,
    isub, istore, ior, iand, iload,
    if_icmpeq, if_icmple, if_icmplt, if_icmpne, if_icmpge,
    if_icmpgt, ifne, GOto, invokestatic, label
}

