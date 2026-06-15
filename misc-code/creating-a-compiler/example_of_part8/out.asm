global _start
_start:
    ;; let
    mov rax, 3
    push rax
    mov rax, 2
    push rax
    mov rax, 3
    push rax
    mov rax, 2
    push rax
    pop rax
    pop rbx
    mul rbx
    push rax
    mov rax, 10
    push rax
    pop rax
    pop rbx
    sub rax, rbx
    push rax
    pop rax
    pop rbx
    div rbx
    push rax
    pop rax
    pop rbx
    add rax, rbx
    push rax
    ;; /let
    ;; let
    mov rax, 0
    push rax
    ;; /let
    ;; if
    mov rax, 1
    push rax
    pop rax
    test rax, rax
    jz label0
    ;; let
    mov rax, 3
    push rax
    mov rax, 2
    push rax
    mov rax, 1
    push rax
    pop rax
    pop rbx
    add rax, rbx
    push rax
    pop rax
    pop rbx
    add rax, rbx
    push rax
    ;; /let
    add rsp, 8
    jmp label1_end
label0:
    ;; elif
    mov rax, 2
    push rax
    pop rax
    test rax, rax
    jz label2
    ;; exit
    mov rax, 2
    push rax
    mov rax, 60
    pop rdi
    syscall
    ;; /exit
    jmp label1_end
label2:
    ;; elif
    mov rax, 22
    push rax
    pop rax
    test rax, rax
    jz label3
    ;; exit
    mov rax, 22
    push rax
    mov rax, 60
    pop rdi
    syscall
    ;; /exit
    jmp label1_end
label3:
    ;; elif
    mov rax, 23
    push rax
    pop rax
    test rax, rax
    jz label4
    ;; exit
    mov rax, 23
    push rax
    mov rax, 60
    pop rdi
    syscall
    ;; /exit
    jmp label1_end
label4:
    ;; else
    ;; exit
    mov rax, 999
    push rax
    mov rax, 60
    pop rdi
    syscall
    ;; /exit
    ;; /else
    ;; /elif
    ;; /elif
    ;; /elif
label1_end:
    ;; /if
    ;; exit
    push QWORD [rsp + 8]
    mov rax, 60
    pop rdi
    syscall
    ;; /exit
    mov rax, 60
    mov rdi, 0
    syscall
