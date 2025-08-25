package org.operatorfoundation.Codex

class Decoder {
}

//class Decoder:
//    def __init__(self, bs):
//self.bs = bs
//
//def encoder(self):
//return Encoder(self.bs)
//
//def decode(self, ns):
//results = []
//for index, b in enumerate(self.bs):
//n = ns[index]
//result = self.decode_step(n, b, index)
//results.append(result)
//
//return sum(results)
//
//def decode_step(self, n, b, index):
//if len(b) == 1:
//print('decode_step({n}, {b}, {index})'.format(n=n, b=b, index=index))
//return 0
//else:
//print('decode_step({n}, {b}, {index})'.format(n=n, b=b, index=index))
//if index == len(self.bs) - 1:
//return b.decode(n)
//else:
//history = self.bs[index + 1:]
//lens = list(map(lambda b: len(b), history))
//p = math.prod(lens)
//print('history: {lens}, p: {p}'.format(lens=lens, p=p))
//result = b.decode(n) * p
//print('result: {result}'.format(result=result))
//return result