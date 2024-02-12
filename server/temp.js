const crypto = require('crypto');

// Read the DER-encoded public key
const derPublicKey = Buffer.from('MIHcMIGUBgkqhkiG9w0BAwEwgYYCQQDzN+HdgosUEF1OMuN+UIv6K/cUsG5fdN/Uw62Pk9GDbcgZBZ7XxgN2nWjP5iza2VNmjL08eNuhdovD6UtrQ0sTAkEA0GdBgoiZXEj4APd23yCCNi+aPTuRNn1whB9DrGEqGVl7bZfH3M612vAOraR8aIMPP0u0YKDybUpukUat/dyEjwNDAAJALEMmXQP+UFRB1Elgs5rr7zwBvszvSM9cXb19WTNh8nGyVwZr0+8ni2Y+LuBAsZWN0j+RKHftBXzX7t4NxRFk1A==', 'base64')


// Load the DER-encoded public key
const publicKey = crypto.createPublicKey({
  key: derPublicKey,
  format: 'der',
  type: 'spki' // SubjectPublicKeyInfo (SPKI) format
});

console.log(publicKey.asymmetricKeyType)


const alice = crypto.createDiffieHellman(512);
console.log('primealice', alice.getPrime())
const aliceKey = alice.generateKeys();
console.log(aliceKey)
const exportedKey = publicKey.export({
	format: 'der',
	type: 'spki'
  });
console.log(exportedKey)
// const aliceSecret = alice.computeSecret(exportedKey);

// Read the DER-encoded public key
const derPublicKey2 = Buffer.from('MIHbMIGTBgkqhkiG9w0BAwEwgYUCQQDyoqbWUsovdPtWWUFXL2jsh+ZjgkLdtY32gIzA0Cy5Rcnb2g5miEC9GCuTNzzNHr2cND/LscAB21BMcDRinT3fAkA8O4IT3ppoctnRzjMsf+KLy62FrCt17cRVq/tb9yg0rHp8gN03skH/a4+hBz9mUB0qrv+4v4m7snl/51CxfeWZA0MAAkApj7FXzmxRWdgHGf4x2u28CO321BajCUyOsIike22VzHWUM1UVg7XxihZma7jYX9N4btBm0up7ADn2FgCSHjZ+', 'base64')


// Load the DER-encoded public key
const publicKey2 = crypto.createPublicKey({
  key: derPublicKey2,
  format: 'der',
  type: 'spki' // SubjectPublicKeyInfo (SPKI) format
});

console.log('extract', publicKey2.symmetricKeySize)
const exportedKey2 = publicKey2.export({
	format: 'der',
	type: "spki"
  });
console.log('anc', exportedKey2)

const bob = crypto.createDiffieHellman(exportedKey2);
console.log('primebob', bob.getPrime())
console.log('primebob', bob.getGenerator())

const param = "7B2270223A2231323534373135353034343038393830323235333539343037383937353937393934383536363437363431343034323139353735373331353932383933323638323834363432363739383937393135363133313239313839393834353237333236383032363737363436393031333636343431373132373532393331313539313432373434303838333130383530383932373339222C2267223A223131333438383336353234333631323034363337323630373931393739343638363233303333333037353931373632303730343132343939353530383637333636323630323437333134393534353533303635313637393837353438323234333436323933313637333930313736303334373735313438383830323138363032353335323930323139313337383236343437373137393136227D"

const buffer = Buffer.from(param, 'hex');
const stringFromHex = JSON.parse(buffer.toString('utf8'));
console.log(stringFromHex.p, stringFromHex.g);
const Eve = crypto.createDiffieHellman(stringFromHex.p, stringFromHex.g)
const evekey = Eve.generateKeys()

const alicekey = "50554240404D4948614D49475342676B71686B6947397730424177457767595143515144766B54646842724D6A73502F61697A716336456C587A704A58396972753279317A78577753695052427966336A0A46794C5835795A313255304C6643554E6869476F35745946774949707364473850714E376F625044416A3833654E497A624169766F327A4C5930445538627563434C6157754C462F6B3476570A696B454E6A6F39457044696E42397536496C7A673133553765486E6A57633033597852616C416F3239416A41317A66736468774451774143514547554350582F3079556571746B666B6D48490A7A39444345614B504339474A5453346A593734797947727771574A4A64503642667A632B494735386E4759727A4C4C53343443777359377841766D427A69622F596E513D0A"

const alicepk = Buffer.from(alicekey, 'hex').toString('utf-8').substring(5)
console.log('alicepk', alicepk)

const alicepkp = crypto.createPublicKey({
  key: Buffer.from(alicepk, 'base64'),
  format: 'der',
  type: 'spki' // SubjectPublicKeyInfo (SPKI) format
});

const cobain = alicepkp.export({
	format: 'der',
	type: "spki"
  });

// console.log(alicepkp)
console.log(Buffer.from(alicepk, 'base64'))
const bobo = crypto.createDiffieHellman(stringFromHex.p, stringFromHex.g)


const bobokey = bobo.generateKeys()
console.log('bobokey', bobokey) 
const cok = Buffer.from('419408f5ffd3251eaad91f9261c8cfd0c211a28f0bd1894d2e2363be32c86af0a9624974fe817f373e206e7c9c662bccb2d2e380b0b18ef102f981ce26ff6274', 'hex')
const evesecret = Eve.computeSecret(cok);
console.log(evesecret)