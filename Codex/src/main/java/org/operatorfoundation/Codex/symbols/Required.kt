package org.operatorfoundation.Codex.Symbols//class Required:
//    def __init__(self, r):
//self.r = r
//
//def __len__(self):
//return 1
//
//def __str__(self):
//return 'Required({r})'.format(r=self.r)
//
//def encode(self, n):
//return self.r
//
//def decode(self, n):
//if n != self.r:
//raise ValueError('Required({r}) != {n}'.format(r=self.r, n=n))
//
//return 0