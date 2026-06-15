// Here we are telling TypeScript that we are certain that this anchor tag exists
const link = document.querySelector('a')!;
console.log(link.href);

// We need to tell TypeScript that we are certain form exists, and that we know it is of type HTMLFormElement.
const form = document.getElementById('signup-form') as HTMLFormElement;;
console.log(form.method);