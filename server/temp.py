import socket, threading
from Crypto.Util.number import *
import base64
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.asymmetric import rsa, dh
from cryptography.hazmat.primitives import serialization, hashes, padding
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
import time
import os
import random
from client_messages import client_messages

responseData = base64.b64decode("MIHaMIGSBgkqhkiG9w0BAwEwgYQCQQDvkTdhBrMjsP/aizqc6ElXzpJX9iru2y1zxWwSiPRByf3jFyLX5yZ12U0LfCUNhiGo5tYFwIIpsdG8PqN7obPDAj83eNIzbAivo2zLY0DU8bucCLaWuLF/k4vWikENjo9EpDinB9u6Ilzg13U7eHnjWc03YxRalAo29AjA1zfsdhwDQwACQEGUCPX/0yUeqtkfkmHIz9DCEaKPC9GJTS4jY74yyGrwqWJJdP6Bfzc+IG58nGYrzLLS44CwsY7xAvmBzib/YnQ=")
holder_public_key = serialization.load_der_public_key(responseData, backend=default_backend())
parameters = holder_public_key.parameters().parameter_numbers()
print(responseData.hex())
# print(parameters.p)
print('p', (parameters.p))
# print(parameters.g)
print('g', (parameters.g))
print('pubnum', holder_public_key.public_numbers().y)
print(holder_public_key.public_bytes(serialization.Encoding.DER, format=serialization.PublicFormat.SubjectPublicKeyInfo).hex())