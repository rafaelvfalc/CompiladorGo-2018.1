100: LD SP, #4000
108: LD R1, #0
116: ST var1, R1

function funcao1
1000: LD R0, #1
1008: BR *0(SP)

function main
4016: ADD SP, SP, #mainsize
4024: ST *SP, #4040
4032: BR #1000
4040: SUB SP, SP, #mainsize
4048: ST var1, R0
