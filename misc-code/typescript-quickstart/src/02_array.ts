let ids: number[] = [1, 2, 3, 4, 5]; // can only contain numbers
let names: string[] = ['Danny', 'Anna', 'Bazza']; // can only contain strings
let options: boolean[] = [true, false, false]; // can only contain true or false
let books: object[] = [
    { name: 'Fooled by randomness', author: 'Nassim Taleb' },
    { name: 'Sapiens', author: 'Yuval Noah Harari' },
]; // can only contain objects
let arr: any[] = ['hello', 1, true]; // any basically reverts TypeScript back into JavaScript

ids.push(6);
// ids.push('7'); // ERROR: Argument of type 'string' is not assignable to parameter of type 'number'.



// let person: (string | number | boolean)[] = ['Danny', 1, true];
// person[0] = 100;

let person = ['Danny', 1, true]; // This is identical to above example
person[0] = 100;
// person[1] = { name: 'Danny' }; // Error - person array can't contain objects
