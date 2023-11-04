l = dlmread('temperature.dat', ',');
l = l';

l = l(1:end-1);
A = zeros(length(l), 3);

A(:,1) = 1;
A(:,2) = 0 : 1 : length(l)-1;
A(:,3) = 0 : 1 : length(l)-1;
A(:,3) = A(:,3).^2;

x_hat = inv(A'*A) * A' * l

time_vec = A(:,2);
t = time_vec;
x2 = x_hat(3)
x1 = x_hat(2)
x0 = x_hat(1)
y = x0 + x1*t + x2*t;

close;
figure;
%plot([l, y], 'LineWidth', 2); hold on;
plot(y);